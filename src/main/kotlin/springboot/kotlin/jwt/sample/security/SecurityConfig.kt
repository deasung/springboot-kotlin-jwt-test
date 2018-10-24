package springboot.kotlin.jwt.sample.security

import io.jsonwebtoken.Jwts
import springboot.kotlin.jwt.sample.security.JwtAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.inject.Inject
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig @Inject constructor(
        val ssImplUserDetailService: SSImplUserDetailService,
        val unauthorizedHandler: JwtAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null
    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsService>(ssImplUserDetailService)
                .passwordEncoder(passwordEncoder())
    }


    override fun configure(httpSecurity: HttpSecurity) {

        // Disable CSRF (cross site request forgery)
        http.csrf().disable().cors().and()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)


        // Entry points
        http.authorizeRequests()//
                .antMatchers("/api/user/token").permitAll()
                .antMatchers("/api/user/login").permitAll()
                .antMatchers("/api/user/accounts").permitAll()
                //                .antMatchers("/api/country").permitAll()
                // Disallow everything else..
                .anyRequest().authenticated()

        val obj = object : OncePerRequestFilter() {
            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
                val requestHeader = request.getHeader(tokenHeader)
                if(requestHeader != null && requestHeader.startsWith("Bearer ")) {
                    val requestToken = requestHeader.substring(7)
                    val claims = Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(requestToken)
                            .body
                    val username = claims.subject
                    if(username != null && SecurityContextHolder.getContext().authentication == null) {
                        val userDetail = userDetailsService().loadUserByUsername(username)
                        if(claims.subject.equals(userDetail.username) && !claims.expiration.before(Date())) {
                            val authentication = UsernamePasswordAuthenticationToken(userDetail, null, userDetail.authorities)
                            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                            SecurityContextHolder.getContext().authentication = authentication
                        }
                    }
                }
                filterChain.doFilter(request, response)
            }
        }


        // Apply JWT
        http.addFilterBefore(obj, BasicAuthenticationFilter::class.java)

        http.headers().cacheControl()


    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
        web
                .ignoring()
                .antMatchers(HttpMethod.POST, "/pub/**")
                .antMatchers(HttpMethod.GET, "/pub/**")
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                )
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**")
    }
}
package springboot.kotlin.jwt.sample.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SSImplUserDetail(
        private val id: String,
        private val pass: String
) : UserDetails {
    override fun getUsername() = id
    override fun getPassword() = pass

    override fun isEnabled() = true
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
}
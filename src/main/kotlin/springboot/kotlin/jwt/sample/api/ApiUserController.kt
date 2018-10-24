package springboot.kotlin.jwt.sample.api

import springboot.kotlin.jwt.sample.common.RespError
import springboot.kotlin.jwt.sample.user.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import springboot.kotlin.jwt.sample.domain.User
import springboot.kotlin.jwt.sample.repository.UserRepository
import springboot.kotlin.jwt.sample.service.UserService
import java.util.*

@RestController
@RequestMapping("/api/user")
class ApiUserController
@Autowired constructor(
        val authenticationManager: AuthenticationManager,
        val userService: UserService,
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder
) {

    @PostMapping(value = ["/accounts"])
    fun accounts(
            @RequestBody reqSignUpDTO: ReqSignUpDTO
    ): ResponseEntity<*> {
        val pass4Store = passwordEncoder.encode(reqSignUpDTO.password)
        val user = userRepository.save(User(
                reqSignUpDTO.id,
                pass4Store,
                reqSignUpDTO.nickName,
                reqSignUpDTO.profileImg,
                Date()
        ))

        return ResponseEntity.ok().build<Any>()
    }


    @PostMapping(value = ["/login"])
    fun login(
            @RequestBody reqSignInDTO: ReqSignInDTO
    ): ResponseEntity<*> {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(reqSignInDTO.id, reqSignInDTO.password))
        } catch (e: Exception) {
            throw when(e) {
                is DisabledException -> {
                    println("User is disabled!")
                    UserException(1, "User is disabled!", e)
                }
                is BadCredentialsException -> {
                    println("Bad credentials!")
                    UserException(2, "Bad credentials!", e)
                }
                is InternalAuthenticationServiceException -> {
                    println("No User!")
                    UserException(3, "No User!", e)
                }
                else -> {
                    println("not known")
                    UserException(4, "not known", e)
                }
            }
        }

        return ResponseEntity.ok(RespSignDTO(
                reqSignInDTO.id,
                userService.generateToken(reqSignInDTO.id),
                Date()
        ))
    }

    @ExceptionHandler(UserException::class)
    fun handler(e: UserException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RespError(e.code, e.message?: "", Date()))
    }
}
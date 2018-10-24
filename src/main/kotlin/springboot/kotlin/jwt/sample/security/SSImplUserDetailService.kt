package springboot.kotlin.jwt.sample.security

import kr.rang2ne.examples.springsecurityjwt.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SSImplUserDetailService @Autowired constructor(
        val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val userOption = userRepository.findById(username)
        return if(userOption.isPresent) {
            val userEntity = userOption.get()
            SSImplUserDetail(userEntity.id, userEntity.password)
        } else {
            null
        }
    }
}
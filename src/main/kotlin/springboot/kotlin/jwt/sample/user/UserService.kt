package springboot.kotlin.jwt.sample.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService @Autowired constructor(
){
    @Value("\${jwt.expiration}")
    private val expiration: Long? = null
    @Value("\${jwt.secret}")
    private val secret: String? = null

    // 토큰 생성
    fun generateToken(id: String): String {
        val claims = HashMap<String, Any>()
        val createdDate = Date()
        val expirationDate = Date(createdDate.time + (expiration!! * 1000))

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(id)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }
}
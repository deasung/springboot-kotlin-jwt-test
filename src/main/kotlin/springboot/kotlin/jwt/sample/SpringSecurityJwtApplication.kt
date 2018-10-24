package springboot.kotlin.jwt.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringSecurityJwtApplication

fun main(args: Array<String>) {
    runApplication<SpringSecurityJwtApplication>(*args)
}

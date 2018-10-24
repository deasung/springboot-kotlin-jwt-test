package springboot.kotlin.jwt.sample.repository

import org.springframework.data.jpa.repository.JpaRepository
import springboot.kotlin.jwt.sample.domain.User

interface UserRepository : JpaRepository<User, String>

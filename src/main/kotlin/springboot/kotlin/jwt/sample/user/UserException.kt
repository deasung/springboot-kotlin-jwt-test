package springboot.kotlin.jwt.sample.user

class UserException(val code: Int, message: String, cause: Throwable) : RuntimeException(message, cause)
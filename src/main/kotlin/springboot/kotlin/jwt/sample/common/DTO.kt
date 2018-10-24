package springboot.kotlin.jwt.sample.common

import java.util.*

data class RespError (
        val code: Int,
        val message: String,
        val time: Date
)
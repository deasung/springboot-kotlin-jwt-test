package springboot.kotlin.jwt.sample.api

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ApiController {

    @PostMapping(value = ["/api/test"])
    fun testApi(): ResponseEntity<*> {
        return ResponseEntity.ok("이 메시지는 인증된 api를 호출한 결과 입니다.")
    }

    @RequestMapping(value = ["/pub/test"], method = [RequestMethod.GET])
    fun image(request: HttpServletRequest, response: HttpServletResponse) {
        val referer = request.getHeader("Referer")
        println(referer)

        val cookies = request.cookies

        val visitedCookie = cookies?.toList()?.find { it.name == "token" }

        if(visitedCookie?.name.isNullOrEmpty()) {
            response.addCookie(Cookie("token", System.currentTimeMillis().toString()))
            println("first")
        } else {
            println("visited")
        }

        val inputStream = ResourceUtils.getFile("classpath:static/imgs/logo.png").inputStream()
        response.contentType = MediaType.IMAGE_PNG_VALUE
        IOUtils.copy(inputStream, response.outputStream)
    }
}
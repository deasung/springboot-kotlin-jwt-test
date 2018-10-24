package springboot.kotlin.jwt.sample.config

import java.util.Arrays

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.RequestMethod

import io.swagger.annotations.ApiOperation
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.ApiKeyVehicle
import springfox.documentation.swagger.web.SecurityConfiguration
import springfox.documentation.swagger2.annotations.EnableSwagger2

import com.google.common.collect.Lists.newArrayList
import springfox.documentation.service.ResponseMessage

@Profile("local")
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .securitySchemes(newArrayList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation::class.java))
                .build().useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        Arrays.asList<ResponseMessage>(ResponseMessageBuilder().code(500)
                                .message("server error")
                                .responseModel(ModelRef("Error")).build()))
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().title("TEST API").description("REST API List")
                .contact(Contact("Test", "test-api.com", "test"))
                .version("1.0.0").build()
    }


    private fun apiKey(): ApiKey {
        return ApiKey("jwt-token", "Authorization", "header")
    }


    @Bean
    internal fun security(): SecurityConfiguration {
        return SecurityConfiguration(// apiKeyName
                null, null, null, null, // appName Needed for authenticate button to work
                "  ", // apiKeyValue
                //                ApiKeyVehicle.HEADER, "Authorization", // apiKeyName
                ApiKeyVehicle.HEADER, "jwt-token", null)// realm Needed for authenticate button to work
    }
}

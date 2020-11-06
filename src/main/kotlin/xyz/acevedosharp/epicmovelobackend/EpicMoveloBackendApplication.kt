package xyz.acevedosharp.epicmovelobackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class EpicMoveloBackendApplication {
    @Bean fun bCrypPasswordEncoder() = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
    runApplication<EpicMoveloBackendApplication>(*args)
}


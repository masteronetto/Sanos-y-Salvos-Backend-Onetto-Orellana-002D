package com.sanosysalvos.bff

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BffServiceApplication

fun main(args: Array<String>) {
    runApplication<BffServiceApplication>(*args)
}

package com.sanosysalvos.pet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PetServiceApplication

fun main(args: Array<String>) {
    runApplication<PetServiceApplication>(*args)
}

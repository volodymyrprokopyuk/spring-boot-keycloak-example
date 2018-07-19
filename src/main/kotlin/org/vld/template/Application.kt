package org.vld.template

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.vld.template.configuration.KeycloakConfiguration

@SpringBootApplication
open class Application

fun main(args: Array<String>) = SpringApplication.run(Application::class.java, *args)

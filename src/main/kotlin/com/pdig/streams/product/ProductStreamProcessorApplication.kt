package com.pdig.streams.product

import com.pdig.streams.product.config.AppConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams

@EnableKafkaStreams
@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration::class)
class ProductStreamProcessorApplication

fun main(args: Array<String>) {
    runApplication<ProductStreamProcessorApplication>(*args)
}

package com.pdig.streams.product.processing

import com.fasterxml.jackson.databind.ObjectMapper
import com.pdig.streams.product.config.json.serde.JacksonSerde
import com.pdig.streams.product.processing.domain.Product
import com.pdig.streams.product.processing.domain.ViewProductEvent
import com.pdig.streams.product.processing.domain.ViewProductEventOutput
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class Stream {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun appTopics(): KafkaAdmin.NewTopics {
        return KafkaAdmin.NewTopics(
            TopicBuilder.name(MAIN).build(),
            TopicBuilder.name(PRODUCT).compact().build(),
            TopicBuilder.name(OUTPUT).build(),
        )
    }

    companion object {
        const val MAIN = "tracking_events_raw"
        const val PRODUCT = "product"
        const val OUTPUT = "tracking_events_products"
    }

    @Bean
    fun topology(
        streamsBuilder: StreamsBuilder,
        processorSupplier: ProcessorSupplier,
        joiner: Joiner,
        jackson: ObjectMapper
    ): KStream<String, ViewProductEventOutput> {

        val serdeKey = Serdes.String()
        val serdeValueOutput = JacksonSerde(jackson, ViewProductEventOutput::class.java)
        val serdeValueProduct = JacksonSerde(jackson, Product::class.java)
        val serdeValueMain = JacksonSerde(jackson, ViewProductEvent::class.java)

        val productConsumedWith: Consumed<String, Product> = Consumed.with(serdeKey, serdeValueProduct)
        val eventConsumedWith: Consumed<String, ViewProductEvent> = Consumed.with(serdeKey, serdeValueMain)
        val producedWith = Produced.with(serdeKey, serdeValueOutput)

        val table = streamsBuilder.table(PRODUCT, productConsumedWith)
        val input = streamsBuilder.stream(MAIN, eventConsumedWith)
        input.peek { key, value -> logger.info("Receive input with key $key and value $value") }
        val joined = input.join(table, joiner)

        joined.peek { key, value -> logger.info("Receive joined with key $key and value $value") }
        joined.to(OUTPUT, producedWith)
        return joined
    }
}

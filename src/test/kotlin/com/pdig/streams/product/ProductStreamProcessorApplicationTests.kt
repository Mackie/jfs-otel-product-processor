package com.pdig.streams.product

import com.pdig.streams.product.config.json.JacksonConfig
import com.pdig.streams.product.config.json.serde.JacksonSerde
import com.pdig.streams.product.processing.Joiner
import com.pdig.streams.product.processing.ProcessorSupplier
import com.pdig.streams.product.processing.Stream
import com.pdig.streams.product.processing.domain.Product
import com.pdig.streams.product.processing.domain.ViewProductEvent
import com.pdig.streams.product.processing.domain.ViewProductEventOutput
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductStreamProcessorApplicationTests {

    private val objectMapper = JacksonConfig().jackson()
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var mainTopic: TestInputTopic<String, ViewProductEvent>
    private lateinit var productTopic: TestInputTopic<String, Product>
    private lateinit var outputTopic: TestOutputTopic<String, ViewProductEventOutput>

    @BeforeEach
    fun before() {

        val streamsBuilder = StreamsBuilder()
        Stream().topology(streamsBuilder, ProcessorSupplier(objectMapper), Joiner(), objectMapper)
        testDriver = TopologyTestDriver(streamsBuilder.build())

        mainTopic = testDriver.createInputTopic(
            Stream.MAIN,
            Serdes.String().serializer(),
            JacksonSerde(objectMapper, ViewProductEvent::class.java).serializer()
        )
        productTopic = testDriver.createInputTopic(
            Stream.PRODUCT,
            Serdes.String().serializer(),
            JacksonSerde(objectMapper, Product::class.java).serializer()
        )
        outputTopic = testDriver.createOutputTopic(
            Stream.OUTPUT,
            Serdes.String().deserializer(),
            JacksonSerde(objectMapper, ViewProductEventOutput::class.java).deserializer()
        )
    }

    @AfterEach
    fun afterEach() {
        testDriver.close()
    }

    @Test
    fun `processor emits correct output event if details topic for vin is received`() {

        val event = objectMapper.readValue(File("src/test/resources/test-event.json"), ViewProductEvent::class.java)
        val product = objectMapper.readValue(File("src/test/resources/product.json"), Product::class.java)

        productTopic.pipeInput(event.productId, product)
        mainTopic.pipeInput(event.productId, event)

        assertFalse(outputTopic.isEmpty)
        outputTopic.readKeyValue().let { keyValue ->
            assertEquals("15656", keyValue.key)
            assertEquals(
                objectMapper.readTree(
                    """
                    {
                       "traceId":"ce5c8581f8f011831ae5cb67803f0820",
                       "eventTime":"2022-12-14T22:22:49.406Z",
                       "product":{
                          "productId": "12345",
                          "category": "pants",
                          "size": "32/32",
                          "brand": "BOSS",
                          "prize": 99.0
                        },
                       "client":{
                          "device":"Mac",
                          "os":{
                             "family":"Mac OS X",
                             "version":"10.15.15"
                          },
                          "agent":{
                             "family":"Chrome",
                             "version":"108.0.0"
                          }
                       },
                       "type":"view_product/1"
                    }
                """.trimIndent()
                ),
                objectMapper.valueToTree(keyValue.value),
            )
        }
    }

}

package com.pdig.streams.product.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.springframework.stereotype.Component

@Component
class ProcessorSupplier(
    private val jackson: ObjectMapper
) : ProcessorSupplier<String, JsonNode, String, JsonNode> {
    override fun get(): Processor<String, JsonNode, String, JsonNode> = Processor(jackson)
}
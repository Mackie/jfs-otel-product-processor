package com.pdig.streams.product.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore

class Processor(private val jackson: ObjectMapper) : Processor<String, JsonNode, String, JsonNode> {

    private lateinit var store: KeyValueStore<String, JsonNode>

    private var partition: Int? = null
    private lateinit var context: ProcessorContext<String, JsonNode>

    override fun init(context: ProcessorContext<String, JsonNode>) {
        this.context = context
        store = context.getStateStore("store")
        partition = if (context.recordMetadata().isPresent) context.recordMetadata().get().partition() else null
    }

    override fun process(record: Record<String, JsonNode>) {
        val value = record.value().deepCopy<JsonNode>() as ObjectNode

        if (partition != null) {
            value.set<JsonNode>("meta", jackson.valueToTree(RecordMeta(partition!!)))
        }
        context.forward(record.withValue(record.value()))
    }

    override fun close() {

    }

    data class RecordMeta(
        val partition: Int
    )
}
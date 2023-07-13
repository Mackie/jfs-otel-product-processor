package com.pdig.streams.product.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class Transformer(
    val jackson: ObjectMapper
) : Transformer<String, JsonNode, KeyValue<String, JsonNode>> {

    private lateinit var store: KeyValueStore<String, JsonNode>

    private var partition by Delegates.notNull<Int>()

    override fun init(context: ProcessorContext) {
        store = context.getStateStore("store")
        partition = context.partition()
    }

    override fun close() {}

    override fun transform(key: String, value: JsonNode): KeyValue<String, JsonNode> {
        return KeyValue(key, value)
    }
}

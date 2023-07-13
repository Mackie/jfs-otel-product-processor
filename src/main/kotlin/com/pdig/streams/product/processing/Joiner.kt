package com.pdig.streams.product.processing

import com.pdig.streams.product.processing.domain.Product
import com.pdig.streams.product.processing.domain.ViewProductEvent
import com.pdig.streams.product.processing.domain.ViewProductEventOutput
import org.apache.kafka.streams.kstream.ValueJoiner
import org.springframework.stereotype.Component

@Component
class Joiner() : ValueJoiner<ViewProductEvent, Product, ViewProductEventOutput> {
    override fun apply(left: ViewProductEvent, right: Product): ViewProductEventOutput {
        return ViewProductEventOutput(
            traceId = left.traceId,
            eventTime = left.eventTime,
            product = right,
            client = left.client
        )
    }
}
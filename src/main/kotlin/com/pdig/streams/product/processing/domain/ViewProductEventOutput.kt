package com.pdig.streams.product.processing.domain

import java.time.Instant

data class ViewProductEventOutput(
    val traceId: String?,
    val eventTime: Instant?,
    val product: Product?,
    val client: Client?,
) {
    val type: String = "view_product/1"
}
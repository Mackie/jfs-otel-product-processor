package com.pdig.streams.product.processing.domain

import java.time.Instant

data class ViewProductEvent(
    val traceId: String?,
    val eventTime: Instant?,
    val productId: String?,
    val client: Client?,
) {
    val type: String = "view_product/1"
}
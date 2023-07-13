package com.pdig.streams.product.processing.domain

data class Product (
    val productId: String,
    val category: String,
    val size: String,
    val brand: String,
    val prize: Double,
)
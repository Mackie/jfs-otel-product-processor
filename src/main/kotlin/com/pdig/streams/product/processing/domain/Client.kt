package com.pdig.streams.product.processing.domain

class Client(
    val device: String?,
    val os: OS?,
    val agent: Agent?
) {
    data class OS(val family: String, val version: String?)
    data class Agent(val family: String, val version: String?)
}
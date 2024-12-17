package com.example.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

suspend fun main(){
    val client = HttpClient(CIO)
    getProduct(client)
}

suspend fun getProduct(client: HttpClient){
    val response: HttpResponse = client.get("https://dummyjson.com/products")
    println(response.status)
    println(response.bodyAsText())
    client.close()
}
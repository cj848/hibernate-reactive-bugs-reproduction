package com.linecorp.kotlinjdsl.spring.data.example

import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "100000")
internal class BookControllerIntegrationTest : WithAssertions {
    @Autowired
    private lateinit var client: WebTestClient

    private val context = "/api/v1/books"

    @Test
    fun createBookMutiny() {
        createBook(BookService.CreateBookSpec("name"), "/mutiny")
    }

    @Test
    fun createBookStage() {
        createBook(BookService.CreateBookSpec("name"), "/stage")
    }

    private fun createBook(spec: BookService.CreateBookSpec, path: String) = client.post().uri(context + path)
        .bodyValue(spec)
        .exchange()
        .expectStatus().isOk
        .returnResult(Long::class.java)
        .responseBody
        .blockFirst()!!
}

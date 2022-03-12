package com.linecorp.kotlinjdsl.spring.data.example

import com.linecorp.kotlinjdsl.spring.data.example.entity.Book
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.future.await
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.stage.Stage
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.concurrent.CompletionStage

@RestController
@RequestMapping("/api/v1/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping("/mutiny")
    suspend fun createBook(@RequestBody spec: BookService.CreateBookSpec): ResponseEntity<Long> =
        bookService.create(spec).let { ResponseEntity.ok().body(it.id) }

    @PostMapping("/stage")
    suspend fun createBookStage(@RequestBody spec: BookService.CreateBookSpec): ResponseEntity<Long> =
        bookService.createStage(spec).let { ResponseEntity.ok().body(it.id) }
}


@Service
class BookService(
    private val mutinySessionFactory: Mutiny.SessionFactory,
    private val stageSessionFactory: Stage.SessionFactory,
) {
    suspend fun create(spec: CreateBookSpec): Book {
        val book = Book(name = spec.name)
        return mutinySessionFactory.withSession { session -> session.persist(book).flatMap { session.flush() } }
            .map { book }
            .awaitSuspending()
    }

    suspend fun createStage(spec: CreateBookSpec): Book {
        val book = Book(name = spec.name)
        return stageSessionFactory.withSession { session -> session.persist(book).thenCompose { session.flush() } }
            .thenApply { book }
            .await()
    }

    data class CreateBookSpec(
        val name: String
    )
}

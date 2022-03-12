package com.linecorp.kotlinjdsl.spring.data.example

import com.linecorp.kotlinjdsl.spring.data.example.entity.Book
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.stage.Stage
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.concurrent.CompletionStage

@RestController
@RequestMapping("/api/v1/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping("/mutiny")
    fun createBook(@RequestBody spec: BookService.CreateBookSpec): Mono<ResponseEntity<Long>> =
        Mono.fromCompletionStage { bookService.create(spec) }.map { ResponseEntity.ok().body(it.id) }

    @PostMapping("/stage")
    fun createBookStage(@RequestBody spec: BookService.CreateBookSpec): Mono<ResponseEntity<Long>> =
        Mono.fromCompletionStage { bookService.createStage(spec) }.map { ResponseEntity.ok().body(it.id) }
}


@Service
class BookService(
    private val mutinySessionFactory: Mutiny.SessionFactory,
    private val stageSessionFactory: Stage.SessionFactory,
) {
    fun create(spec: CreateBookSpec): CompletionStage<Book> {
        val book = Book(name = spec.name)
        return mutinySessionFactory.withSession { session -> session.persist(book).flatMap { session.flush() } }
            .map { book }
            .subscribeAsCompletionStage()
    }

    fun createStage(spec: CreateBookSpec): CompletionStage<Book> {
        val book = Book(name = spec.name)
        return stageSessionFactory.withSession { session -> session.persist(book).thenCompose { session.flush() } }
            .thenApply { book }
    }

    data class CreateBookSpec(
        val name: String
    )
}

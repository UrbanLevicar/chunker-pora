package com.example.chuckerdemo.server

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun currentTime(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(Date())

data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val createdAt: String? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)

data class FactorialRequest(
    val number: Int
)

data class FactorialResponse(
    val factorial: Long
)

class ApiServer {

    private var server: NettyApplicationEngine? = null
    private val posts = mutableListOf(
        Post(1, "Prvi post", "Prvi post body", 1),
        Post(2, "Drugi post", "Drugi post body", 1),
        Post(3, "Tretji post", "Tretji post body", 2)
    )

    fun start(port: Int = 8080) {
        CoroutineScope(Dispatchers.IO).launch {
            server = embeddedServer(Netty, port = port) {
                install(ContentNegotiation) {
                    gson {
                        setPrettyPrinting()
                    }
                }

                routing {
                    configureRoutes()
                }
            }.start(wait = false)

            println("API Server teče na: http://localhost:$port")
        }
    }

    private fun Routing.configureRoutes() {

        // GET /api/posts - vsi posti
        get("/api/posts") {
            call.respond(posts)
        }

        // GET /api/posts/{id} - en post
        get("/api/posts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val post = posts.find { it.id == id }

            if (post != null) {
                call.respond(post)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
            }
        }

        // POST /api/posts - ustvari post
        post("/api/posts") {
            val newPost = call.receive<Post>()
            val postWithId = newPost.copy(
                id = posts.maxOfOrNull { it.id }?.plus(1) ?: 1,
                createdAt = currentTime()
            )
            posts.add(postWithId)
            call.respond(HttpStatusCode.Created, postWithId)
        }

        // PUT /api/posts/{id} - posodobi post
        put("/api/posts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedPost = call.receive<Post>()

            val index = posts.indexOfFirst { it.id == id }
            if (index != -1) {
                posts[index] = updatedPost.copy(id = id!!)
                call.respond(posts[index])
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
            }
        }

        // DELETE /api/posts/{id} - izbriši post
        delete("/api/posts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val removed = posts.removeIf { it.id == id }

            if (removed) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
            }
        }

        // GET /api/users - vsi uporabniki
        get("/api/users") {
            val users = listOf(
                User(1, "Janez Novak", "janez@example.com"),
                User(2, "Ana Kovač", "ana@example.com"),
                User(3, "Marko Horvat", "marko@example.com")
            )
            call.respond(users)
        }

        // GET /api/users/{id} - en uporabnik
        get("/api/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val users = listOf(
                User(1, "Janez Novak", "janez@example.com"),
                User(2, "Ana Kovač", "ana@example.com"),
                User(3, "Marko Horvat", "marko@example.com")
            )
            val user = users.find { it.id == id }

            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
        }

        // GET /api/error - simulacija napake
        get("/api/error") {
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Simulirana napaka!")
            )
        }

        // GET /api/slow - počasen odgovor (za testiranje timeout)
        get("/api/slow") {
            delay(5000) // 5 sekund
            call.respond(mapOf("message" to "To je bil počasen odgovor"))
        }

        // GET /api/health - health check
        get("/api/health") {
            call.respond(mapOf(
                "status" to "OK",
                "timestamp" to currentTime(),
                "postsCount" to posts.size
            ))
        }

        post("/api/calculateFactorial") {
            val request = try { call.receive<FactorialRequest>() } catch (e: Exception) { null }
            val number = request?.number
            if (number != null) {
                if (number < 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Number must be non-negative"))
                } else {
                    val result = calculateFactorial(number)
                    call.respond(FactorialResponse(result))
                }
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid input: Please provide a valid integer in JSON as {\"number\": <int>}.")
                )
            }
        }
    }
    fun calculateFactorial(n: Int): Long {
        return if (n <= 1) 1L else n.toLong() * calculateFactorial(n - 1)
    }

    fun stop() {
        server?.stop(1000, 2000)
        println("API Server ustavljen")
    }

    fun isRunning(): Boolean = server != null

    fun getBaseUrl(port: Int = 8080): String = "http://localhost:$port"
}
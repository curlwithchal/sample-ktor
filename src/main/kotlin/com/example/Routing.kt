package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.repo.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("Exception Error : ${cause.message}")
        }
    }
    routing {
        staticResources("/dog", "mydog")
        get("/") {
            call.respondText("Hello World!")
        }
        get("/cat") {
            val text = "<h1>Hello Cat!!</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text = text, contentType = type)
        }
        get("/error-test") {
            throw IllegalStateException("Busy")
        }
        staticResources("/task-ui", "task-ui")
        staticResources("static", "static")
        staticResources("statictwo", "statictwo")
        //thymeleaf
        route("/tasks") {
            get {
                val tasks = TaskRepository.allTasks()
                call.respond(
                    ThymeleafContent("all-tasks", mapOf("tasks" to tasks))
                )
            }
            get("/byName") {
                val name = call.request.queryParameters["name"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(
                    ThymeleafContent("single-task", mapOf("task" to task))
                )
            }
            get("/byPriority") {
                val priorityAsText = call.request.queryParameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.taskByPriority(priority)


                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    val data = mapOf(
                        "priority" to priority,
                        "tasks" to tasks
                    )
                    call.respond(ThymeleafContent("task-by-priority", data))
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post {
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )
                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val priority = Priority.valueOf(params.third)
                    TaskRepository.addTask(
                        Task(
                            params.first,
                            params.second,
                            priority
                        )
                    )
                    val tasks = TaskRepository.allTasks()
                    call.respond(
                        ThymeleafContent("all-tasks", mapOf("tasks" to tasks))
                    )
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        //Restful
        /* route("/tasks") {
             get {
                 val tasks = TaskRepository.allTasks()
                 call.respond(tasks)
             }
             get("/byName/{taskName}") {
                 val name = call.parameters["taskName"]
                 if (name == null) {
                     call.respond(HttpStatusCode.BadRequest)
                     return@get
                 }
                 val taskByName = TaskRepository.taskByName(name)
                 if (taskByName == null) {
                     call.respond(HttpStatusCode.NotFound)
                     return@get
                 }
                 call.respond(taskByName)
             }
             get("/byPriority/{priority}") {
                 val priorityAsText = call.parameters["priority"]
                 if (priorityAsText == null) {
                     call.respond(HttpStatusCode.BadRequest)
                     return@get
                 }
                 try {
                     val priority = Priority.valueOf(priorityAsText)
                     val tasks = TaskRepository.taskByPriority(priority)
                     if (tasks.isEmpty()) {
                         call.respond(HttpStatusCode.NotFound)
                         return@get
                     }
                     call.respond(tasks)
                 } catch (e: IllegalArgumentException) {
                     call.respond(HttpStatusCode.BadRequest)
                 }
             }
             delete("/{taskName}") {
                 val name = call.parameters["taskName"]
                 if (name == null) {
                     call.respond(HttpStatusCode.BadRequest)
                     return@delete
                 }
                 if (TaskRepository.removeTask(name)) {
                     call.respond(HttpStatusCode.NoContent)
                 } else {
                     call.respond(HttpStatusCode.NotFound)
                 }
             }
             post {
                 try {
                     val task = call.receive<Task>()
                     TaskRepository.addTask(task)
                     call.respond(HttpStatusCode.Created)
                 } catch (e: IllegalArgumentException) {
                     call.respond(HttpStatusCode.BadRequest)
                 } catch (e: IllegalStateException) {
                     call.respond(HttpStatusCode.BadRequest)
                 }
             }
         }*/
    }
}

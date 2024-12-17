import com.example.model.Priority
import com.example.model.Task
import com.example.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TaskAp {

    @Test
    fun `task can be found by priority`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Medium")
        val result = response.body<List<Task>>()

        assertEquals(HttpStatusCode.OK, response.status)
        val expectedTaskName = listOf("gardening", "painting")
        val actualTaskName = result.map(Task::name)
        assertEquals(expectedTaskName, actualTaskName)
    }

    @Test
    fun `invalid priority Bad Request 400 `() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/byPriority/hellocatnotfound")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid priority Not Found 404 `() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `add task`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val task = Task("Java", "Hello Java", Priority.High)
        val responsePost = client.post("/tasks") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(task)
        }

        assertEquals(HttpStatusCode.Created, responsePost.status)

        val responseResult = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, responseResult.status)
        val body = responseResult.body<List<Task>>().map { it.name }

        assertContains(body, "Java")

    }

}
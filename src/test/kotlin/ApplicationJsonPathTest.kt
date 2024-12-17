import com.example.model.Priority
import com.example.module
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationJsonPathTest {

    @Test
    fun `task can be found`() = testApplication {
        application {
            module()
        }

        val jsonDoc = client.getAsJsonPath("/tasks")
        val result: List<String> = jsonDoc.read("$[*].name")
        assertEquals("cleaning", result[0])
        assertEquals("gardening", result[1])
        assertEquals("shopping", result[2])

    }

    @Test
    fun `task can be found by priority`() = testApplication {
        application {
            module()
        }

        val priorityName = Priority.Medium
        val jsonDoc = client.getAsJsonPath("/tasks/byPriority/$priorityName")
        val result: List<String> = jsonDoc.read("$[?(@.priority == '$priorityName')]['name']")
        assertEquals(2, result.size)
        assertEquals("gardening", result[0])
        assertEquals("painting", result[1])

    }

    suspend fun HttpClient.getAsJsonPath(url: String): DocumentContext {
        val response = this.get(url) {
            accept(ContentType.Application.Json)
        }
        return JsonPath.parse(response.bodyAsText())
    }
}
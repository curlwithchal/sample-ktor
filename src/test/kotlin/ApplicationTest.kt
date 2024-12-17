import com.example.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testApp() = testApplication {
        application {
            module()
        }
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testCat() = testApplication {
        application {
            module()
        }
        val response = client.get("/cat")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "Hello Cat!!")
    }

    @Test
    fun testStatusPageError() = testApplication {
        application {
            module()
        }
        val response = client.get("/error-test")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Exception Error : Busy", response.bodyAsText())
    }
}
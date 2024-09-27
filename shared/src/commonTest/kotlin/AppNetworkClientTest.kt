import com.daniel.myapplication.data.AppNetworkClient
import com.daniel.myapplication.data.AppNetworkClientException
import io.ktor.client.call.body
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.engine.mock.*
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/* TEST CASES
- Successful Response
- 404 Not Found Error
- 500 Internal Server Error
- Request Timeout Error
- Invalid JSON Format Handling
- Custom Exception Handling for Non-Successful Status Codes
 */

class AppNetworkClientTest {

    @Test
    fun `test successful response`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"message": "Success"}""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()


        val response = httpClient.get("/test")
        val responseBody = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"message": "Success"}""", responseBody)
    }

    @Test
    fun `test 404 not found response`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"errors":["Not found"]}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val exception = assertFailsWith<AppNetworkClientException> {
            val response = httpClient.get("/not-found")
        }

        assertTrue(exception.message!!.contains("HTTP ERROR - Code: 404"))
    }

    @Test
    fun `test 500 internal server error`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"errors":["Internal Server Error"]}""",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val exception = assertFailsWith<AppNetworkClientException> {
            val response = httpClient.get("/server-error")
        }

        assertTrue(exception.message!!.contains("HTTP ERROR - Code: 500"))
    }

    @Test
    fun `test request timeout error`() = runBlocking {
        val mockEngine = MockEngine { request ->
            throw SocketTimeoutException("Request timed out")
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val exception = assertFailsWith<SocketTimeoutException> {
            val response = httpClient.get("/timeout")
        }

        assertTrue(exception.message!!.contains("Request timed out"))
    }

    @Test
    fun `test invalid json response handling`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{invalid json}""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val exception = assertFailsWith<Exception> {
            val response = httpClient.get("/invalid-json")
            response.body<MockData>()
        }

        assertTrue(exception.message!!.contains("Serializer for class 'MockData' is not found."))
    }

    @Test
    fun `test custom exception for non-successful status codes`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"errors":["Unauthorized"]}""",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val exception = assertFailsWith<AppNetworkClientException> {
            val response = httpClient.get("/unauthorized")
        }

        assertTrue(exception.message!!.contains("HTTP ERROR - Code: 401"))
    }

    @Test
    fun `test custom header injection`() = runBlocking {
        val mockEngine = MockEngine { request ->
            assertEquals("Bearer test-token", request.headers[HttpHeaders.Authorization])
            respond(
                content = """{"message": "Success"}""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val appNetworkClient = AppNetworkClient(mockEngine)
        val httpClient = appNetworkClient.appHttpClient()

        val response = httpClient.get("/test") {
            header(HttpHeaders.Authorization, "Bearer test-token")
        }

        val responseBody = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"message": "Success"}""", responseBody)
    }
}

data class MockData(val message: String, val code: Int)
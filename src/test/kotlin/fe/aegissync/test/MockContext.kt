package fe.aegissync.test

import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.http.HttpStatus
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier


open class MockContext(
    val httpServletRequest: HttpServletRequest = MockHttpServletRequest(),
    val httpServletResponse: HttpServletResponse = MockHttpServletResponse()
) : Context {
    override fun <T> appAttribute(key: String): T {
        throw NotImplementedError("Not mocked")
    }

    override fun endpointHandlerPath(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun future(future: Supplier<out CompletableFuture<*>>) {
        throw NotImplementedError("Not mocked")
    }

    override fun handlerType(): HandlerType {
        throw NotImplementedError("Not mocked")
    }

    override fun matchedPath(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun outputStream(): ServletOutputStream {
        throw NotImplementedError("Not mocked")
    }

    override fun pathParam(key: String): String {
        throw NotImplementedError("Not mocked")
    }

    override fun pathParamMap(): Map<String, String> {
        throw NotImplementedError("Not mocked")
    }

    override fun redirect(location: String, status: HttpStatus) {
        throw NotImplementedError("Not mocked")
    }

    override fun req(): HttpServletRequest {
        return httpServletRequest
    }

    override fun res(): HttpServletResponse {
        return httpServletResponse
    }

    override fun result(resultStream: InputStream): Context {
        throw NotImplementedError("Not mocked")
    }

    override fun resultInputStream(): InputStream? {
        throw NotImplementedError("Not mocked")
    }
}

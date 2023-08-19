package fe.aegissync.module.web.endpoint.base

import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import java.net.HttpURLConnection.HTTP_BAD_REQUEST

open class Response(private val text: String, private val status: Int) {
    open class WithErrorResponse(text: String, status: Int, private val errorText: String? = null) :
        Response(text, status) {
        override fun toMap() = super.toMap().apply {
            if (errorText != null) this["error"] = errorText
        }
    }

    class InvalidBody(errorText: String? = null) : WithErrorResponse(
        "Invalid body data", HttpStatus.BAD_REQUEST_400, errorText
    ) {
        companion object {
            val Default = InvalidBody()
        }
    }

    class InvalidParameter(errorText: String? = null) : WithErrorResponse(
        "Invalid body data", HTTP_BAD_REQUEST, errorText
    ) {
        companion object {
            val Default = InvalidParameter()
        }
    }

    class InvalidAuthentication(errorText: String? = null) : WithErrorResponse(
        "Invalid authentication", HttpStatus.UNAUTHORIZED_401, errorText
    ) {
        companion object {
            val Default = InvalidAuthentication()
        }
    }

    class InternalError(errorText: String? = null) : WithErrorResponse(
        "Internal error", HttpStatus.INTERNAL_SERVER_ERROR_500, errorText
    ) {
        companion object {
            val Default = InvalidAuthentication()
        }
    }

    open fun toMap(): MutableMap<String, String> {
        return mutableMapOf("response" to text)
    }

    fun send(ctx: Context) {
        ctx.status(status).json(toMap())
    }

}

fun Context.send(response: Response) = response.send(this)

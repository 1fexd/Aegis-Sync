package fe.aegissync.test

import jakarta.servlet.http.Part
import java.io.InputStream

open class MockPart : Part {
    override fun getInputStream(): InputStream {
        throw NotImplementedError("Not mocked")
    }

    override fun getContentType(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getName(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getSubmittedFileName(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getSize(): Long {
        throw NotImplementedError("Not mocked")
    }

    override fun write(fileName: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun delete() {
        throw NotImplementedError("Not mocked")
    }

    override fun getHeader(name: String?): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getHeaders(name: String?): MutableCollection<String> {
        throw NotImplementedError("Not mocked")
    }

    override fun getHeaderNames(): MutableCollection<String> {
        throw NotImplementedError("Not mocked")
    }
}

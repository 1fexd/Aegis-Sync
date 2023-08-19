package fe.aegissync.test

import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.io.PrintWriter
import java.util.*

open class MockHttpServletResponse : HttpServletResponse {
    override fun getCharacterEncoding(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getContentType(): String {
        throw NotImplementedError("Not mocked")
    }

    override fun getOutputStream(): ServletOutputStream {
        throw NotImplementedError("Not mocked")
    }

    override fun getWriter(): PrintWriter {
        throw NotImplementedError("Not mocked")
    }

    override fun setCharacterEncoding(charset: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun setContentLength(len: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun setContentLengthLong(len: Long) {
        throw NotImplementedError("Not mocked")
    }

    override fun setContentType(type: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun setBufferSize(size: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun getBufferSize(): Int {
        throw NotImplementedError("Not mocked")
    }

    override fun flushBuffer() {
        throw NotImplementedError("Not mocked")
    }

    override fun resetBuffer() {
        throw NotImplementedError("Not mocked")
    }

    override fun isCommitted(): Boolean {
        throw NotImplementedError("Not mocked")
    }

    override fun reset() {
        throw NotImplementedError("Not mocked")
    }

    override fun setLocale(loc: Locale?) {
        throw NotImplementedError("Not mocked")
    }

    override fun getLocale(): Locale {
        throw NotImplementedError("Not mocked")
    }

    override fun addCookie(cookie: Cookie?) {
        throw NotImplementedError("Not mocked")
    }

    override fun containsHeader(name: String?): Boolean {
        throw NotImplementedError("Not mocked")
    }

    override fun encodeURL(url: String?): String {
        throw NotImplementedError("Not mocked")
    }

    override fun encodeRedirectURL(url: String?): String {
        throw NotImplementedError("Not mocked")
    }

    override fun encodeUrl(url: String?): String {
        throw NotImplementedError("Not mocked")
    }

    override fun encodeRedirectUrl(url: String?): String {
        throw NotImplementedError("Not mocked")
    }

    override fun sendError(sc: Int, msg: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun sendError(sc: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun sendRedirect(location: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun setDateHeader(name: String?, date: Long) {
        throw NotImplementedError("Not mocked")
    }

    override fun addDateHeader(name: String?, date: Long) {
        throw NotImplementedError("Not mocked")
    }

    override fun setHeader(name: String?, value: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun addHeader(name: String?, value: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun setIntHeader(name: String?, value: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun addIntHeader(name: String?, value: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun setStatus(sc: Int) {
        throw NotImplementedError("Not mocked")
    }

    override fun setStatus(sc: Int, sm: String?) {
        throw NotImplementedError("Not mocked")
    }

    override fun getStatus(): Int {
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

package lofitsky.komptube.local.ui

import lofitsky.komptube.common.Mode
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.FutureTask
import kotlin.system.exitProcess


sealed class ServiceController(private val backend: BACKEND) {
    object LocalServiceController : ServiceController(BACKEND.LOCAL)
    object PhoneServiceController : ServiceController(BACKEND.PHONE)

    companion object {
        private const val LOCAL_BACKEND_URL_DEFAULT = "http://localhost:8765"
        private const val PHONE_BACKEND_URL_DEFAULT = "http://192.168.1.3:8765"

        enum class BACKEND { LOCAL, PHONE }

        val httpClient: OkHttpClient by lazy { OkHttpClient() }
    }

    private val properties by lazy {
        System.getenv()
    }

    private val backendUrl: String by lazy {
        when(backend) {
            BACKEND.LOCAL -> properties["urls.local_backend"]?.takeIf { it.isNotBlank() } ?: LOCAL_BACKEND_URL_DEFAULT
            BACKEND.PHONE -> properties["urls.phone"]?.takeIf { it.isNotBlank() } ?: PHONE_BACKEND_URL_DEFAULT
        }
    }

    fun checkHealth(): Boolean = sendRequest("mode=${Mode.HEALTH_CHECK}")

    fun sendForOpening(urlDecoded: String) {
        val urlEncoded = URLEncoder.encode(urlDecoded, StandardCharsets.UTF_8)
        sendRequest("mode=${Mode.SHARE_TO_PHONE}&url=$urlEncoded")
        exitProcess(0)
    }

    private fun sendRequest(decodedUrlParams: String): Boolean {
        val job = FutureTask {
            try {
                val request: Request = Request.Builder()
                        .get()
                        .url("$backendUrl/?$decodedUrlParams")
                    .build()

                val response = httpClient.newCall(request).execute()
                    .also { println("response: code = ${it.code}, body = ${it.body?.string()}") }
                response.code == 200
            } catch(_: Exception) {
                false
            }
        }

        return try {
            Thread(job)
                .also { it.start() }
                .join(5000)
            job.get()
        } catch(e: Exception) {
            false
        }
    }
}

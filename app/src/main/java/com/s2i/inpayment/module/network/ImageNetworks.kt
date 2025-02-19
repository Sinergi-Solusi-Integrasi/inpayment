import android.util.Log
import com.s2i.data.local.auth.SessionManager
import okhttp3.Interceptor

class RequestHeaderInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = sessionManager.accessToken
        val originalRequest = chain.request()
        val modifiedRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.e("AuthInterceptor", "Error: Token is null or empty")
            originalRequest
        }
        return chain.proceed(modifiedRequest)
    }
}
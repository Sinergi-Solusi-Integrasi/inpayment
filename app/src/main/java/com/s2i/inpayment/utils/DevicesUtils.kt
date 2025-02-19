package com.s2i.inpayment.utils

import android.util.Log
import com.s2i.data.local.auth.SessionManager
import org.json.JSONObject

object DevicesUtils {

    fun processDeviceResponse(response: String, token: String, sessionManager: SessionManager)
    {
        try {
            val jsonObject = JSONObject(response)
            val dataObject = jsonObject.getJSONObject("data")
            val deviceId = dataObject.getString("device_id")
            val brand = dataObject.getString("model")
            val osType = dataObject.getString("os")
            val platform = dataObject.getString("platform")
            val sdkVersion = dataObject.getString("sdk_version")
//            val deviceVersion = "Unknown" // Default jika tidak tersedia

            sessionManager.createDeviceToken(
                devicesId = deviceId,
                deviceToken = token,
                deviceBrand = brand,
                deviceModel = brand,
                osType = osType,
                devicePlatform = platform,
                sdkVersion = sdkVersion,
            )
            Log.d("DeviceUtils", "Device ID saved: $deviceId")
        } catch (e: Exception) {
            Log.e("DeviceUtils", "Failed to parse device response: ${e.message}", e)
        }
    }
}
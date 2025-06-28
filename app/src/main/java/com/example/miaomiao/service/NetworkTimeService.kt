package com.example.miaomiao.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class WorldTimeResponse(
    val datetime: String,
    val timezone: String,
    val utc_datetime: String
)

interface WorldTimeApi {
    @GET("timezone/Asia/Shanghai")
    suspend fun getChinaTime(): WorldTimeResponse
}

class NetworkTimeService(
    private val context: Context
) {
    private val api = Retrofit.Builder()
        .baseUrl("http://worldtimeapi.org/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WorldTimeApi::class.java)
    
    private var cachedNetworkTime: LocalDateTime? = null
    private var cacheTimestamp: Long = 0
    private val cacheValidityMs = 5 * 60 * 1000L // 5分钟缓存
    
    suspend fun getCurrentTime(): LocalDateTime {
        return if (isNetworkAvailable() && shouldRefreshCache()) {
            try {
                getChinaTime()
            } catch (e: Exception) {
                // 网络获取失败，使用本地时间转换为中国时区
                getChinaLocalTime()
            }
        } else {
            // 使用缓存的网络时间或本地时间
            cachedNetworkTime ?: getChinaLocalTime()
        }
    }
    
    private suspend fun getChinaTime(): LocalDateTime = withContext(Dispatchers.IO) {
        val response = api.getChinaTime()
        val chinaTime = LocalDateTime.parse(
            response.datetime.substring(0, 19),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        
        // 只保留年月日，时间设为00:00:00
        val dateOnly = chinaTime.toLocalDate().atStartOfDay()
        
        // 缓存网络时间
        cachedNetworkTime = dateOnly
        cacheTimestamp = System.currentTimeMillis()
        
        dateOnly
    }
    
    private fun getChinaLocalTime(): LocalDateTime {
        // 获取当前本地时间并转换为中国时区
        val chinaZone = ZoneId.of("Asia/Shanghai")
        val chinaTime = ZonedDateTime.now(chinaZone).toLocalDateTime()
        
        // 只保留年月日，时间设为00:00:00
        return chinaTime.toLocalDate().atStartOfDay()
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    private fun shouldRefreshCache(): Boolean {
        return cachedNetworkTime == null || 
               (System.currentTimeMillis() - cacheTimestamp) > cacheValidityMs
    }
    
    fun isUsingNetworkTime(): Boolean {
        return cachedNetworkTime != null && 
               (System.currentTimeMillis() - cacheTimestamp) <= cacheValidityMs
    }
}
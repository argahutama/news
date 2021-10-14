package com.argahutama.repository.extension

import org.json.JSONObject
import retrofit2.Response

fun Response<*>.getErrorInfo(): String {
    val errorBodyString = errorBody()?.string() ?: return message()
    var message: String? = null
    val jsonObject: JSONObject?
    try {
        jsonObject = JSONObject(errorBodyString)
        message = jsonObject.getJSONObject("meta").getString("message")
    } catch (e: Exception) {
    }
    if (message.isNullOrEmpty()) message = message()
    return message!!
}

fun <T, R> Response<T>.tryToReturn(mapper: (T?) -> R) =
    if (isSuccessful) mapper(body())
    else throw Exception(getErrorInfo())
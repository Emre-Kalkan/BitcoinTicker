package com.emrekalkan.bitcointicker.utils

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null,
    val exception: Throwable? = null
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String, exception: Throwable? = null, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message, exception)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
package com.project.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    // Observer yang akan menyimpan data dan menghentikan CountDownLatch
    val observer = Observer<T> { value ->
        data = value
        latch.countDown()
    }

    this.observeForever(observer) // Mengamati LiveData tanpa memperhatikan lifecycle

    try {
        afterObserve.invoke() // Panggil tindakan tambahan setelah observasi dimulai

        // Tunggu sampai LiveData memberikan nilai
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer) // Hapus observer setelah pengujian
    }

    @Suppress("UNCHECKED_CAST")
    return data as T // Mengembalikan data yang sudah di-cast
}

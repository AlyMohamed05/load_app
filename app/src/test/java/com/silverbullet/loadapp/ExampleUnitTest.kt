package com.silverbullet.loadapp

import com.silverbullet.loadapp.api.RetrofitApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testApiRequests() {
        runBlocking {
            val req1 = async { RetrofitApi.instance.getRetrofitPage() }
            val req2 = async { RetrofitApi.instance.getGlidePage() }
            val req3 = async { RetrofitApi.instance.getStarterProjectPage() }
            assertEquals(200, req1.await().code())
            assertEquals(200, req2.await().code())
            assertEquals(200, req3.await().code())
        }
    }
}
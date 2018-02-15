package com.zacharee1.systemuituner.util

import android.util.Log
import java.io.*

object SuUtils {
    @JvmStatic
    fun sudo(vararg strings: String) {
        try {
            val su = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(su.outputStream)

            for (s in strings) {
                outputStream.writeBytes(s + "\n")
                outputStream.flush()
            }

            outputStream.writeBytes("exit\n")
            outputStream.flush()
            try {
                su.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e("No Root?", e.message)
            }

            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun testSudo(): Boolean {
        var st: StackTraceElement? = null

        try {
            val su = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(su.outputStream)

            outputStream.writeBytes("exit\n")
            outputStream.flush()

            val inputStream = DataInputStream(su.inputStream)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            while (bufferedReader.readLine() != null) {
                bufferedReader.readLine()
            }

            su.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
            for (s in e.stackTrace) {
                st = s
                if (st != null) break
            }
        }

        return st == null
    }
}

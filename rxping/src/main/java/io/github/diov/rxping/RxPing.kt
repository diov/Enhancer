package io.github.diov.rxping

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import java.util.LinkedList

/**
 * Created by Dio_V on 2019-05-30.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

object RxPing {

    private val TIME_REGEX = """\d+ bytes from .+icmp.+ttl.+time=([\d.]+) ms""".toRegex()

    /**
     * Ping one ip for multi times.
     *
     * @param host which host to ping.
     * @param count "-c" parameter for ping command.
     * @param timeout "-w" parameter for ping command.
     * @return an pair which first element is t, and second is a list of ping result.
     */
    fun ping(host: String, count: Int, timeout: Int): Observable<LinkedList<Double>> {
        return Observable.just(host)
            .observeOn(Schedulers.io())
            .map transform1@{
                val command = listOf("/system/bin/ping", "-c", count.toString(), "-w", timeout.toString(), host)
                val process = ProcessBuilder(command).redirectErrorStream(true).start()
                return@transform1 InputStreamReader(process.inputStream)
            }
            .map transform2@{ reader ->
                val result: LinkedList<Double> = LinkedList()

                val bufferedReader = reader.buffered()
                bufferedReader.forEachLine { line ->
                    println(line)
                    result.add(parseTime(line) ?: 0.toDouble())
                }
                reader.close()

                return@transform2 result
            }
    }

    private fun parseTime(line: String): Double? {
        val matchGroup = TIME_REGEX.matchEntire(line)?.groups
        return matchGroup?.get(1)?.value?.toDoubleOrNull()
    }
}

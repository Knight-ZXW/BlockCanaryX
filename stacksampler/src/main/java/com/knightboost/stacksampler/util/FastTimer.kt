package com.knightboost.stacksampler.util

import android.os.SystemClock

class FastTimer {

    companion object {
        @JvmStatic
        private val initialRTCTime: Long = System.currentTimeMillis()
        @JvmStatic
        private val initialElapsedRealTime: Long = SystemClock.elapsedRealtime()

        @JvmStatic
        fun currentTimeMillis(): Long {
            return initialRTCTime + (SystemClock.elapsedRealtime() - initialElapsedRealTime)
        }

        @JvmStatic
        fun elapsedRealtime(): Long {
            return SystemClock.elapsedRealtime()
        }

        @JvmStatic
        fun convertRTCTimeToElapseRealTime(rtcTime: Long): Long {
            return rtcTime - initialRTCTime + initialElapsedRealTime
        }

        @JvmStatic
        fun convertElapseRealTimeToRTCTime(elapsedRealTime: Long): Long {
            return initialRTCTime + (elapsedRealTime - initialElapsedRealTime)
        }

    }

}
package com.knightboost.stacksampler.demo

import android.os.Looper
import com.knightboost.stacksampler.StackSampler

class MainThreadStackSampler {
    companion object {
        private val stackSampler = StackSampler(
            Looper.getMainLooper().thread,
            50
        )

        @JvmStatic
        public fun get(): StackSampler {
            return stackSampler
        }
    }

}
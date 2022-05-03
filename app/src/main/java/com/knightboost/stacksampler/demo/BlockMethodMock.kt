package com.knightboost.stacksampler.demo

object BlockMethodMock {
    fun blockMethod1() {
        Thread.sleep(40)
    }

    fun blockMethod2() {
        Thread.sleep(360)
    }

    fun ioWork() {
        blockMethod1()
        Thread.sleep(500)
        blockMethod2()
        blockMethod3()
    }

    fun blockMethod3() {
        Thread.sleep(1080)
    }
}
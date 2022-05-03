package blockcanary

import java.util.concurrent.Executor
import java.util.concurrent.Executors

object CanaryExecutors {
    val workExecutor: Executor = Executors.newSingleThreadExecutor()

}
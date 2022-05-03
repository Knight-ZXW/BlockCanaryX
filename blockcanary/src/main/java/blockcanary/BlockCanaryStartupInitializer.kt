package blockcanary

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import blockcanary.R;

internal class BlockCanaryStartupInitializer : Initializer<BlockCanaryStartupInitializer> {


    override fun create(context: Context)= apply {
        val application = context.applicationContext as Application
        val autoInstall = application.resources.getBoolean(R.bool.block_canary_auto_install)
        if (autoInstall){
            val blockCanaryConfig = BlockCanaryConfig.newBuilder().build()
            BlockCanary
                .install(application,blockCanaryConfig)
        }
    }

    override fun dependencies()= emptyList<Class<out Initializer<*>>>()
}
package blockcanary

import android.os.*

class LooperUtil {
    companion object {
        @JvmStatic
        public fun getMessageOfQueueHead(looper: Looper): Message? {
            val mQueueField = looper.javaClass.getDeclaredField("mQueue")
            mQueueField.isAccessible = true
            val queue: MessageQueue = mQueueField.get(looper) as MessageQueue
            val mMessagesField = queue.javaClass.getDeclaredField("mMessages")
            mMessagesField.isAccessible = true
            val message: Message? = mMessagesField.get(queue) as Message?;
            return message;
        }

    }


}
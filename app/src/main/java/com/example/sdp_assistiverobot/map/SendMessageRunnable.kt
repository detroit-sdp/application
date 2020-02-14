package com.example.sdp_assistiverobot.map

class SendMessageRunnable constructor(private val mTask: NetworkTask, private val out: String) : Runnable {

    val TAG = "SendMessageRunnable"

    companion object {
        const val SEND_FAILURE = -1
        const val SEND_START = 0
        const val SEND_SUCCEED = 1
    }

    interface SendMessageInterface{
        fun setSendThread(currentThread: Thread)
        fun getSendThread(): Thread
        fun handleSendState(state: Int)
    }

    override fun run() {
        // Move the current thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)

        mTask.setSendThread(Thread.currentThread())

        try {
            if(Thread.interrupted()) {
                return
            }
            mTask.handleSendState(SEND_START)

            if(Thread.interrupted()) {
                return
            }
            val socket = mTask.openSocket()
            val sendPackage = mTask.createPackage(out)

            if(Thread.interrupted()) {
                return
            }
            socket.send(sendPackage)
            mTask.handleSendState(SEND_SUCCEED)
        } catch (e: Exception) {
            mTask.handleSendState(SEND_FAILURE)
        }


    }
}
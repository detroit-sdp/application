package com.example.sdp_assistiverobot.map

import java.io.PrintWriter

class SendMessageRunnable(output: PrintWriter): Runnable {

    override fun run() {
        // Move the current thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)

    }
}
package app.studera.android.service

import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class StuderaMessagingService : FirebaseMessagingService() {

    private val TAG = "firebase messaging"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "received message from firebase")
    }
}
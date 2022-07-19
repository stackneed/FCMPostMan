package com.firebase.test

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.messaging.ktx.messaging


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.initialize(this)
        Firebase.messaging.subscribeToTopic("all")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]

        if (intent.hasExtra("APP_LINK")) {
            val appPKG = intent.getStringExtra("APP_LINK")!!

            Log.e(TAG, "PKg Received: $appPKG")

            isAppInstalledOrNot(appPKG)
        }
    }

    private fun isAppInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            val launch = packageManager.getLaunchIntentForPackage(uri)
            startActivity(launch)

            true
        } catch (e: PackageManager.NameNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$uri")
                )
            )
            false
        }
    }

/*

  ///Process for PostMan

https://fcm.googleapis.com/fcm/send

////////Add to the body ->raw ->Json

    { "to":"/topics/all",
        "data" : {
        "icon" : "",
        "title" : "",
        "short_desc" : "",
        "feature" : "",
        "app_url" : "com.whatsapp"
    }
    }

///into the header

Authorization    key=server key from firebase cloud messaging
Content-Type     application/json

    */

}
package ch.temparus.powerroot.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import ch.temparus.powerroot.services.notifications.DefaultNotification

/**
 * An [Service] subclass for asynchronously monitor battery
 * state while charging in a service on a separate handler thread.
 */
class ProximityService : Service() {

    private var mConfiguration: SharedPreferences? = null

    override fun onCreate() {
        Log.d("ProximityService", "Service started!")
        ProximityService.running = true

        mConfiguration = PreferenceManager.getDefaultSharedPreferences(this)
        startForeground(DefaultNotification.NOTIFICATION_ID, DefaultNotification.get(this))
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(true)

        running = false
        Log.d("ProximityService", "Service stopped!")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        // SharedPreferences
        const val PREF_ENABLED = "proximityEnabled"
        const val PREF_WAVE_MODE = "proximityWaveMode"
        const val PREF_POCKET_MODE = "proximityPocketMode"
        const val PREF_LOCK_SCREEN = "proximityLockScreen"
        const val PREF_LOCK_SCREEN_LANDSCAPE = "proximityLockScreenLandscape"
        const val PREF_LOCK_SCREEN_COVER_TIME = "proximityLockScreenCoverTime"

        private var running = false

        /**
         * Returns whether the service is running right now
         *
         * @return Whether service is running
         */
        fun isRunning(): Boolean {
            return running
        }

        fun start(context: Context) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ProximityService.PREF_ENABLED, false)) {
                Handler().post({ context.startService(Intent(context, ProximityService::class.java)) })
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ProximityService::class.java))
        }
    }
}
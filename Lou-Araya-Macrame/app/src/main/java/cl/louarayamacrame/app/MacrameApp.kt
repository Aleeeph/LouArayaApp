package cl.louarayamacrame.app

import android.app.Application
import cl.louarayamacrame.app.di.AppContainer
import cl.louarayamacrame.app.notifications.NotificationHelper

class MacrameApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
    }
}

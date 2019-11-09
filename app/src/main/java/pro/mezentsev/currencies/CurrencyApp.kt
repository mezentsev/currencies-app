package pro.mezentsev.currencies

import android.app.Application
import pro.mezentsev.currencies.di.component.AppComponent
import pro.mezentsev.currencies.di.component.DaggerAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

class CurrencyApp : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        component = DaggerAppComponent
            .factory()
            .create(this)
    }
}
package pro.mezentsev.currencies

import android.app.Application
import pro.mezentsev.currencies.di.component.AppComponent
import pro.mezentsev.currencies.di.component.DaggerAppComponent

class CurrencyApp : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
            .factory()
            .create(this)
    }
}
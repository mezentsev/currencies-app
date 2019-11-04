package pro.mezentsev.currencies.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import pro.mezentsev.currencies.di.module.RepositoryModule
import pro.mezentsev.currencies.di.module.RetrofitModule
import pro.mezentsev.currencies.di.scope.PerApplication

@PerApplication
@Component(modules = [
    RetrofitModule::class,
    RepositoryModule::class
])
interface AppComponent {

    fun currencyFactory(): CurrencyComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}
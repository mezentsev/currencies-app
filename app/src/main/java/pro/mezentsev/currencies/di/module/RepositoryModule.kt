package pro.mezentsev.currencies.di.module

import dagger.Module
import dagger.Provides
import pro.mezentsev.currencies.data.CurrencyRepository
import pro.mezentsev.currencies.data.CurrencyRepositoryImpl
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.di.scope.PerApplication

@Module
class RepositoryModule {

    @Provides
    @PerApplication
    fun provideCurrencyRepository(currencyApi: CurrencyApi): CurrencyRepository =
        CurrencyRepositoryImpl(currencyApi)

}
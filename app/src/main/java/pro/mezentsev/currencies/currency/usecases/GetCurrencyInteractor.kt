package pro.mezentsev.currencies.currency.usecases

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pro.mezentsev.currencies.data.CurrencyRepository
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetCurrencyInteractor @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    fun getCurrency(rate: Rate): Observable<Currency> =
        currencyRepository
            .getCurrency(rate)
            .subscribeOn(Schedulers.io())
            .repeatWhen { it.delay(1, TimeUnit.SECONDS) }

}
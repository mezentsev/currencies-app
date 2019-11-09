package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate

interface CurrencyRepository {

    fun getCurrency(rate: Rate): Observable<Currency>

}
package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.model.Currency

interface CurrencyRepository {

    fun getCurrency(base: String, amount: Double): Observable<Currency>

}
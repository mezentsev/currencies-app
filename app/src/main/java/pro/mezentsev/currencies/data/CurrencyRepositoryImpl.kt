package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import kotlin.math.roundToInt

class CurrencyRepositoryImpl constructor(private val currencyApi: CurrencyApi) :
    CurrencyRepository {

    override fun getCurrency(base: String, amount: Double): Observable<Currency> {
        return currencyApi
            .getLatest(base)
            .map { response ->
                Currency(
                    response.base,
                    response.rates.map { (base, value) ->
                        Rate(base, (value * amount * 100.0).roundToInt() / 100.0)
                    })
            }
    }

}
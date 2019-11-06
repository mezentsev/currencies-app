package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.currency.usecases.ConvertCurrencyInteractor
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import java.math.BigDecimal

class CurrencyRepositoryImpl(
    private val currencyApi: CurrencyApi,
    private val convertCurrencyInteractor: ConvertCurrencyInteractor
) :
    CurrencyRepository {

    override fun getCurrency(base: String, amount: String): Observable<Currency> {
        return currencyApi
            .getLatest(base)
            .map { response ->
                val rates = response.rates.map { (base, value) ->
                    Rate(base, value.toBigDecimal(), value.toString())
                }

                convertCurrencyInteractor.convert(rates, Rate(response.base, BigDecimal.ONE, amount))
            }
    }
}
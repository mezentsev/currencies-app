package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.api.CurrencyResponse
import pro.mezentsev.currencies.currency.usecases.ConvertCurrencyInteractor
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CurrencyRepositoryImpl(
    private val currencyApi: CurrencyApi,
    private val convertCurrencyInteractor: ConvertCurrencyInteractor
) : CurrencyRepository {

    @Volatile
    private var currentResponse: CurrencyResponse? = null

    override fun getCurrency(rate: Rate): Observable<Currency> {
        return Observable
            .concat(provideSavedResponse(), provideApiResponse(rate))
            .map { response -> convertCurrency(rate, response) }
    }

    private fun provideApiResponse(rate: Rate): Observable<CurrencyResponse> {
        return currencyApi
            .getLatest(rate.base)
            .delay(1, TimeUnit.SECONDS)
            .map(this::enrichResponse)
            .doOnNext { response ->
                currentResponse = response.copy()
            }
    }

    private fun provideSavedResponse(): Observable<CurrencyResponse> {
        return currentResponse
            ?.let { Observable.just(it) }
            ?: Observable.empty()
    }

    private fun enrichResponse(response: CurrencyResponse): CurrencyResponse {
        if (!response.rates.containsKey(response.base)) {
            val rates =  mutableMapOf(response.base to 1.0)
            rates.putAll(response.rates)
            return response.copy(rates = rates)
        }

        return response
    }

    private fun convertCurrency(rate: Rate, response: CurrencyResponse): Currency {
        val rates = response.rates.map { (base, value) ->
            Rate(base, value.toBigDecimal(), value.toString())
        }

        return convertCurrencyInteractor.convert(
            rate.base, rates, Rate(rate.base, BigDecimal.ONE, rate.typedValue)
        )
    }
}
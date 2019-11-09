package pro.mezentsev.currencies.data

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.api.CurrencyResponse
import pro.mezentsev.currencies.currency.usecases.ConvertCurrencyInteractor
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate

class CurrencyRepositoryTest {
    private val currencyApi = mock<CurrencyApi>()
    private val convertCurrencyInteractor = mock<ConvertCurrencyInteractor>()

    private val underTest: CurrencyRepository = CurrencyRepositoryImpl(
        currencyApi,
        convertCurrencyInteractor
    )

    @Test
    fun `apply typed amount to rates`() {
        val base = "EUR"
        val rate1 = Rate("EUR", 133.20.toBigDecimal(), "133.20")
        val rate2 = Rate("USD", 142.2.toBigDecimal(), "142.2")
        val rates = listOf(rate1, rate2)
        val date = "12345678"
        val typedAmount = "2.0"

        val currencyResponse = CurrencyResponse(
            base, date,
            mapOf(
                Pair("EUR", 66.6),
                Pair("USD", 71.1)
            )
        )
        val currencyObs = Observable.just(currencyResponse)
        val currency = Currency(base, rates)

        whenever(convertCurrencyInteractor.convert(any(), any(), any())).thenReturn(currency)
        whenever(currencyApi.getLatest(any())).thenReturn(currencyObs)

        val latest = underTest.getCurrency(base, typedAmount).blockingFirst()

        assertEquals(base, latest.base)
        latest.rates.forEachIndexed { index, rate ->
            assertEquals(rates[index], rate)
        }

        verify(currencyApi).getLatest(eq(base))
    }
}

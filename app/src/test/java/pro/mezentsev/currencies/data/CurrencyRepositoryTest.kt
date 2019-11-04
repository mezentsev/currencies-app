package pro.mezentsev.currencies.data

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.api.CurrencyResponse
import pro.mezentsev.currencies.model.Rate

class CurrencyRepositoryTest {
    private val currencyApi = mock<CurrencyApi>()

    private val underTest: CurrencyRepository = CurrencyRepositoryImpl(currencyApi)

    @Test
    fun `apply typed amount to rates`() {
        val base = "EUR"
        val rate1 = Rate("EUR", 66.6)
        val rate2 = Rate("USD", 71.1)
        val date = "12345678"
        val typedAmount = 2.0

        val rates = listOf(rate1, rate2)
        val currencyResponse = CurrencyResponse(
            base, date,
            mapOf(Pair(rate1.base, rate1.value), Pair(rate2.base, rate2.value))
        )
        val currencyObs = Observable.just(currencyResponse)

        whenever(currencyApi.getLatest(any())).thenReturn(currencyObs)

        val latest = underTest.getCurrency(base, typedAmount).blockingFirst()

        assertEquals(base, latest.base)
        latest.rates.forEachIndexed { index, rate ->
            assertEquals(rates[index].value * typedAmount, rate.value, 0.01)
            assertEquals((rates[index].value * typedAmount).toString(), rate.typedValue)
            assertEquals(rates[index].base, rate.base)
        }

        verify(currencyApi).getLatest(eq(base))
    }
}

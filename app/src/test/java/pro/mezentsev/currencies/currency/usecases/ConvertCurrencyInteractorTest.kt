package pro.mezentsev.currencies.currency.usecases

import org.junit.Test
import pro.mezentsev.currencies.model.Rate
import kotlin.test.assertEquals

class ConvertCurrencyInteractorTest {

    private val underTest: ConvertCurrencyInteractor = ConvertCurrencyInteractor()

    @Test
    fun `apply new currency for existed list`() {

        val rates = listOf(
            Rate("EUR", 2.0.toBigDecimal(), "2.0"),
            Rate("RUB", 3.0.toBigDecimal(), "3.0"),
            Rate("USD", 4.0.toBigDecimal(), "4.0")
        )
        val multiply = "9.1"
        val multiplyDecimal = "9.1".toBigDecimal()
        val newRate = Rate("DNM", 1.0.toBigDecimal(), multiply)

        val currency = underTest.convert(rates, newRate)

        assertEquals(newRate.base, currency.base)

        assertEquals(4, currency.rates.size)
        assertEquals(newRate.typedValue, currency.rates[0].typedValue)
        assertEquals(newRate.base, currency.rates[0].base)

        currency.rates.drop(1).forEachIndexed { index, rate ->
            assertEquals(rates[index].base, rate.base)
            assertEquals(0, rates[index].value.compareTo(rate.value))
            assertEquals(
                0,
                rates[index].typedValue.toBigDecimal().multiply(multiplyDecimal).compareTo(rate.typedValue.toBigDecimal())
            )
        }
    }
}

package pro.mezentsev.currencies.currency.usecases

import org.junit.Test
import pro.mezentsev.currencies.model.Rate
import java.math.BigDecimal
import kotlin.test.assertEquals

class ConvertCurrencyInteractorTest {

    private val underTest: ConvertCurrencyInteractor = ConvertCurrencyInteractor()

    @Test
    fun `apply new currency for existed list then change typed value`() {

        val rates = listOf(
            Rate("EUR", 2.toBigDecimal(), "2"),
            Rate("RUB", 3.toBigDecimal(), "3"),
            Rate("USD", 4.toBigDecimal(), "4")
        )
        val multiply = "9"
        val multiplyDecimal = multiply.toBigDecimal()
        val newRate = Rate("DNM", 1.toBigDecimal(), multiply)

        var currency = underTest.convert("EUR", rates, newRate)

        assertEquals(newRate.base, currency.base)

        assertEquals(4, currency.rates.size)
        assertEquals(newRate.typedValue, currency.rates[0].typedValue)
        assertEquals(newRate.base, currency.rates[0].base)

        checkRates(rates, currency.rates.drop(1), multiplyDecimal)

        // change typed value
        val secondMultiply = "19"
        val secondMultiplyDecimal = "19".toBigDecimal()
        val secondRate = Rate("DNM", 1.0.toBigDecimal(), secondMultiply)
        currency = underTest.convert("EUR", currency.rates, secondRate)

        assertEquals(4, currency.rates.size)
        assertEquals(secondRate.typedValue, currency.rates[0].typedValue)
        assertEquals(secondRate.base, currency.rates[0].base)

        checkRates(rates, currency.rates.drop(1), secondMultiplyDecimal)
    }

    @Test
    fun `keep the save rates after N convert operations`() {

        val EUR = Rate("EUR", 1.toBigDecimal(), "1")
        val RUB = Rate("RUB", 3.toBigDecimal(), "3")
        val USD = Rate("USD", 4.toBigDecimal(), "4")

        val rates = listOf(EUR, RUB, USD)

        val initRate = EUR
        val anotherRate = USD

        var currency = underTest.convert("EUR", rates, initRate)
        currency = underTest.convert("EUR", currency.rates, anotherRate)
        currency = underTest.convert("USD", currency.rates, initRate)

        checkRates(rates, currency.rates, initRate.value)
    }

    @Test
    fun `clean all amounts`() {
        val EUR = Rate("EUR", 1.toBigDecimal(), "1")
        val RUB = Rate("RUB", 3.toBigDecimal(), "3")
        val USD = Rate("USD", 4.toBigDecimal(), "4")

        val ZERO_EUR_AMOUNT = EUR.copy(typedValue = "")

        val rates = listOf(EUR, RUB, USD)

        val initRate = EUR
        val anotherRate = ZERO_EUR_AMOUNT

        var currency = underTest.convert("EUR", rates, initRate)
        currency = underTest.convert("EUR", currency.rates, anotherRate)

        checkRates(rates, currency.rates, BigDecimal.ZERO)
    }

    @Test
    fun `new currency with wrong typed value`() {
        val EUR = Rate("EUR", 1.toBigDecimal(), "1")
        val RUB = Rate("RUB", 3.toBigDecimal(), "3")
        val USD = Rate("USD", 4.toBigDecimal(), "4")
        val WRONG_CURRENCY = Rate("PPC", BigDecimal.ZERO, "$#$#")

        val rates = listOf(EUR, RUB, USD)
        val expectedRates = listOf(
            WRONG_CURRENCY.copy(value = BigDecimal.ONE, typedValue = ""), EUR, RUB, USD
        )

        val currency = underTest.convert(EUR.base, rates, WRONG_CURRENCY)

        checkRates(expectedRates, currency.rates, BigDecimal.ZERO)
    }

    private fun checkRates(expectedRates: List<Rate>,
                           actualRates: List<Rate>,
                           typedValueDecimal: BigDecimal) {
        actualRates.forEachIndexed { index, rate ->
            assertEquals(expectedRates[index].base, rate.base)
            assertEquals(
                0,
                expectedRates[index].value.compareTo(rate.value),
                "Expected decimal '${expectedRates[index].value}' but got '${rate.value}'"
                )

            val expectedTypedValue = try {
                expectedRates[index].typedValue
                    .toBigDecimal()
                    .multiply(typedValueDecimal)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
            val actualTypedValue = try {
                rate.typedValue.toBigDecimal()
            } catch (e: Exception) {
                BigDecimal.ZERO
            }

            assertEquals(
                0,
                expectedTypedValue.compareTo(actualTypedValue),
                "Expected decimal '$expectedTypedValue' but got '$actualTypedValue'"
            )
        }
    }
}

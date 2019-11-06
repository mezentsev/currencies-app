package pro.mezentsev.currencies.currency.usecases

import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ConvertCurrencyInteractor @Inject constructor() {

    fun convert(currentRates: List<Rate>, baseRate: Rate): Currency {
        val convertedBase = baseRate.base
        val bigDecimalAmount = try {
            baseRate.typedValue.toBigDecimal()
        } catch (e: Exception) {
            BigDecimal(0)
        }

        val existedBaseRate = currentRates.firstOrNull { it.base == baseRate.base }
        val convertedRates = mutableListOf(
            Rate(baseRate.base, BigDecimal.ONE, baseRate.typedValue)
        )
        convertedRates.addAll(
            currentRates
                .filterNot { it.base == baseRate.base }
                .map { (base, value) ->
                    val convertedValue = value
                        .divide(existedBaseRate?.value ?: BigDecimal.ONE, RoundingMode.HALF_UP)

                    val bigDecimalTypedValue = bigDecimalAmount
                        .multiply(convertedValue)

                    val typedValue = if (bigDecimalTypedValue.compareTo(BigDecimal.ZERO) == 0) {
                        ""
                    } else {
                        bigDecimalTypedValue
                            .setScale(2, BigDecimal.ROUND_HALF_DOWN)
                            .toString()
                    }

                    Rate(base, convertedValue, typedValue)
                }
                .sortedBy { it.base })

        return Currency(convertedBase, convertedRates)
    }

}
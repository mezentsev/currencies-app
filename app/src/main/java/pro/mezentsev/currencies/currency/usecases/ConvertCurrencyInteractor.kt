package pro.mezentsev.currencies.currency.usecases

import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import pro.mezentsev.currencies.util.v
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ConvertCurrencyInteractor @Inject constructor() {

    fun convert(currentBase: String, currentRates: List<Rate>, newRate: Rate): Currency {

        val convertedBase = newRate.base
        val currentRateForOldBase = currentRates.firstOrNull { it.base == currentBase } ?: Rate(currentBase, BigDecimal.ONE, "1")
        val oldRateForNewBase = currentRates.firstOrNull{ it.base == newRate.base} ?: Rate(newRate.base, BigDecimal.ONE, "1")

        val newRateTypedBigDecimal = try {
            newRate.typedValue
                .toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
        val newRateForNewBase = Rate(newRate.base, BigDecimal.ONE, newRate.typedValue)

        val convertedRates = mutableListOf(newRateForNewBase)
        convertedRates.addAll(
            currentRates
                .filterNot { it.base == newRate.base }
                .map { (base, value) ->
                    val convertedRate = value.divide(oldRateForNewBase.value, 6, RoundingMode.HALF_UP)
                    val convertedTypedValueBigDecimal = convertedRate
                        .multiply(newRateTypedBigDecimal)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .stripTrailingZeros()
                    val convertedTypedValue = if (convertedTypedValueBigDecimal.compareTo(BigDecimal.ZERO) == 0)
                        "" else convertedTypedValueBigDecimal.toString()

                    "$base: $value / ${oldRateForNewBase.value} = $convertedRate => '$convertedTypedValue'".v()

                    Rate(base, convertedRate, convertedTypedValue)
                }
                .sortedBy { it.base }
        )

        return Currency(convertedBase, convertedRates)
    }

}
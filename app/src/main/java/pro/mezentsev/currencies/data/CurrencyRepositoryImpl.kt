package pro.mezentsev.currencies.data

import io.reactivex.Observable
import pro.mezentsev.currencies.api.CurrencyApi
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import java.math.BigDecimal

class CurrencyRepositoryImpl constructor(private val currencyApi: CurrencyApi) :
    CurrencyRepository {

    override fun getCurrency(base: String, amount: String): Observable<Currency> {
        val bigDecimalAmount = try {
            amount.toBigDecimal()
        } catch (e: Exception) {
            BigDecimal(0)
        }

        return currencyApi
            .getLatest(base)
            .map { response ->
                Currency(
                    response.base,
                    response.rates.map { (base, value) ->
                        val bigDecimalTypedValue = bigDecimalAmount
                            .multiply(value.toBigDecimal())
                            .setScale(2, BigDecimal.ROUND_HALF_DOWN)

                        val typedValue = if (bigDecimalTypedValue.compareTo(BigDecimal.ZERO) == 0) {
                            ""
                        } else {
                            bigDecimalTypedValue.toString()
                        }

                        Rate(base, typedValue)
                    })
            }
    }

}
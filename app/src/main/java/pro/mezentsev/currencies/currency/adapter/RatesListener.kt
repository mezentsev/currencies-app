package pro.mezentsev.currencies.currency.adapter

interface RatesListener {
    fun onClicked(base: String, currentValue: Double)

    fun onRatesChanged(base: String, typedValue: String)
}
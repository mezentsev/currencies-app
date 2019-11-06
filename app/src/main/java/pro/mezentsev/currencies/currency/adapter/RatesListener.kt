package pro.mezentsev.currencies.currency.adapter

import pro.mezentsev.currencies.model.Rate

interface RatesListener {
    fun onClicked(rate: Rate)

    fun onRatesChanged(rate: Rate)
}
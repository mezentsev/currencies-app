package pro.mezentsev.currencies.currency.adapter

import pro.mezentsev.currencies.model.Rate

interface RatesListener {
    fun onRatesChanged(rate: Rate)
}
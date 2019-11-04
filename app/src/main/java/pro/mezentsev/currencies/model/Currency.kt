package pro.mezentsev.currencies.model

data class Currency(val base: String, val rates: List<Rate>)
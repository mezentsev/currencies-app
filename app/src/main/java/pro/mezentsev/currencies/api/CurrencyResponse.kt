package pro.mezentsev.currencies.api

data class CurrencyResponse(val base: String,
                            val date: String,
                            val rates: Map<String, Double>)
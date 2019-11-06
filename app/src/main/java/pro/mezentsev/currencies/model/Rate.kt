package pro.mezentsev.currencies.model

import java.math.BigDecimal

data class Rate(val base: String, val value: BigDecimal, val typedValue: String)
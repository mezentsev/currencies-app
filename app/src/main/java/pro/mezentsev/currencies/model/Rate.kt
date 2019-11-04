package pro.mezentsev.currencies.model

data class Rate(
    val base: String,
    val value: Double,
    val typedValue: String = if (value == .0) "" else value.toString()
)
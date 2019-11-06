package pro.mezentsev.currencies.currency

import pro.mezentsev.currencies.base.Contract
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate

interface CurrencyContract {
    interface View : Contract.BaseView {
        fun showCurrency(
            currency: Currency,
            typedValue: String
        )

        fun showProgress()

        fun showError(base: String)
    }

    abstract class Presenter : Contract.BasePresenter<View>() {
        abstract fun load(base: String = "EUR")

        abstract fun ratesChanged(rate: Rate)
    }
}
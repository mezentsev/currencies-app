package pro.mezentsev.currencies.currency

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pro.mezentsev.currencies.currency.usecases.GetCurrencyInteractor
import pro.mezentsev.currencies.di.scope.PerCurrency
import pro.mezentsev.currencies.model.Currency
import javax.inject.Inject

@PerCurrency
class CurrencyPresenter @Inject constructor(
    private val getCurrencyInteractor: GetCurrencyInteractor
) : CurrencyContract.Presenter() {

    private val subscriptions = CompositeDisposable()
    private var amount = 1.0
    private var typedValue = "1.0"

    private var currency: Currency? = null

    override fun load(base: String) {
        subscriptions.clear()
        view?.showProgress()

        val subscribe = getCurrencyInteractor
            .getCurrency(base, amount)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ currency ->
                this.currency = currency
                view?.showCurrency(currency, amount, typedValue)
            }, {
                Log.e(TAG, "Can't get currencies", it)
                view?.showError(base)
            })

        subscriptions.add(subscribe)
    }

    override fun ratesChanged(base: String, typedValue: String) {
        this.amount = try {
            typedValue.toDouble()
        } catch (e: Exception) {
            0.0
        }

        this.typedValue = if (amount == 0.0) "" else typedValue
        load(base)
    }

    override fun detach() {
        subscriptions.clear()
        super.detach()
    }

    companion object {
        const val TAG: String = "CurrencyPresenter"
    }
}
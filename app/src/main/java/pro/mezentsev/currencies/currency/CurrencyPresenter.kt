package pro.mezentsev.currencies.currency

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pro.mezentsev.currencies.currency.usecases.GetCurrencyInteractor
import pro.mezentsev.currencies.di.scope.PerCurrency
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import javax.inject.Inject

@PerCurrency
class CurrencyPresenter @Inject constructor(
    private val getCurrencyInteractor: GetCurrencyInteractor
) : CurrencyContract.Presenter() {

    private val subscriptions = CompositeDisposable()
    private var typedValue = "1.0"

    private var currency: Currency? = null

    override fun load(base: String) {
        subscriptions.clear()
        view?.showProgress()

        val subscribe = getCurrencyInteractor
            .getCurrency(base, typedValue)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ currency ->
                this.currency = currency
                view?.showCurrency(currency, typedValue)
            }, {
                Log.e(TAG, "Can't get currencies", it)
                view?.showError(base)
            })

        subscriptions.add(subscribe)
    }

    override fun ratesChanged(rate: Rate) {
        this.typedValue = rate.typedValue
        load(rate.base)
    }

    override fun detach() {
        subscriptions.clear()
        super.detach()
    }

    companion object {
        const val TAG: String = "CurrencyPresenter"
    }
}
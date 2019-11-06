package pro.mezentsev.currencies.currency

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pro.mezentsev.currencies.currency.usecases.ConvertCurrencyInteractor
import pro.mezentsev.currencies.currency.usecases.GetCurrencyInteractor
import pro.mezentsev.currencies.di.scope.PerCurrency
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import javax.inject.Inject

@PerCurrency
class CurrencyPresenter @Inject constructor(
    private val getCurrencyInteractor: GetCurrencyInteractor,
    private val convertCurrencyInteractor: ConvertCurrencyInteractor
) : CurrencyContract.Presenter() {

    private val subscriptions = CompositeDisposable()
    private var rate: Rate? = null

    private var currency: Currency? = null

    override fun load(base: String) {
        subscriptions.clear()
        view?.showProgress()

        val typedValue = rate?.typedValue ?: "1"

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
        if (this.rate == rate) {
            return
        }

        subscriptions.clear()
        this.rate = rate

        var currentCurrency = currency
        if (currentCurrency != null) {
            currentCurrency = convertCurrencyInteractor.convert(currentCurrency.rates, rate)
            currency = currentCurrency

            view?.showCurrency(currentCurrency, rate.typedValue)
        }

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
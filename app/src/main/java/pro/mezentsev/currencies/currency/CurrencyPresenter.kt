package pro.mezentsev.currencies.currency

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pro.mezentsev.currencies.currency.usecases.GetCurrencyInteractor
import pro.mezentsev.currencies.di.scope.PerCurrency
import pro.mezentsev.currencies.model.Rate
import pro.mezentsev.currencies.util.e
import java.math.BigDecimal
import javax.inject.Inject

@PerCurrency
class CurrencyPresenter @Inject constructor(
    private val getCurrencyInteractor: GetCurrencyInteractor
) : CurrencyContract.Presenter() {

    private val subscriptions = CompositeDisposable()

    private var rate: Rate? = null

    override fun load(base: String, typedValue: String) {
        subscriptions.clear()
        view?.showProgress()

        val subscribe = getCurrencyInteractor
            .getCurrency(Rate(base, BigDecimal.ONE, typedValue))
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribe({ currency ->
                view?.showCurrency(currency, typedValue)
            }, { error ->
                error.e(text = "Can't get currencies")
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

        load(rate.base, rate.typedValue)
    }

    override fun detach() {
        subscriptions.clear()
        super.detach()
    }
}
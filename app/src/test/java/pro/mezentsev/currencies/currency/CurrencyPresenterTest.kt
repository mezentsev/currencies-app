package pro.mezentsev.currencies.currency

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import pro.mezentsev.currencies.currency.usecases.ConvertCurrencyInteractor
import pro.mezentsev.currencies.currency.usecases.GetCurrencyInteractor
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate

class CurrencyPresenterTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    private val getCurrencyinteractor = mock<GetCurrencyInteractor>()
    private val convertCurrencyInteractor = mock<ConvertCurrencyInteractor>()
    private val underTest: CurrencyContract.Presenter = CurrencyPresenter(getCurrencyinteractor, convertCurrencyInteractor)

    @Test
    fun `get currency from repository on load`() {
        val view = mock<CurrencyContract.View>()
        val base = "USD"

        whenever(getCurrencyinteractor.getCurrency(any(), any()))
            .thenReturn(Observable.just(mock()))

        underTest.attach(view)
        underTest.load(base)

        verify(getCurrencyinteractor).getCurrency(eq(base), eq("1"))
    }

    @Test
    fun `show currency from repository`() {
        val view = mock<CurrencyContract.View>()
        val base = "USD"
        val currency = Currency(
            base,
            listOf(mock())
        )
        val currencyObs = Observable.just(currency)

        whenever(getCurrencyinteractor.getCurrency(any(), any())).thenReturn(currencyObs)

        underTest.attach(view)
        underTest.load(base)

        currencyObs.blockingNext()

        verify(view).showProgress()
        verify(view, never()).showError(any())
        verify(view).showCurrency(eq(currency), eq("1"))
    }

    @Test
    fun `load new currency on rates changed by typing`() {
        val view = mock<CurrencyContract.View>()
        val base = "RUB"
        val typedAmount = "156"
        val rates = listOf(Rate("EUR", 66.6.toBigDecimal(), "66.6"))
        val currency = Currency(
            base,
            rates
        )
        val currencyObs = Observable.just(currency)

        whenever(getCurrencyinteractor.getCurrency(any(), any())).thenReturn(currencyObs)

        underTest.attach(view)
        underTest.ratesChanged(Rate(base, typedAmount.toBigDecimal(), typedAmount))

        verify(view).showProgress()
        verify(view, never()).showError(any())
        verify(view).showCurrency(eq(currency), eq(typedAmount))
    }

    @Test
    fun `change typed amount to empty if value is not valid`() {
        val view = mock<CurrencyContract.View>()
        val base = "RUB"
        val brokenAmount = "000"
        val typedAmount = ""
        val rates = listOf(Rate("EUR", 66.6.toBigDecimal(), brokenAmount))
        val currency = Currency(
            base,
            rates
        )
        val currencyObs = Observable.just(currency)

        whenever(getCurrencyinteractor.getCurrency(any(), any())).thenReturn(currencyObs)

        underTest.attach(view)
        underTest.ratesChanged(Rate(base, 1.0.toBigDecimal(), brokenAmount))

        verify(view).showProgress()
        verify(view, never()).showError(any())
        verify(view).showCurrency(eq(currency), eq(brokenAmount))
    }
}

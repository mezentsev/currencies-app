package pro.mezentsev.currencies.currency.usecases

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.currency.CurrencyContract
import pro.mezentsev.currencies.currency.CurrencyPresenter
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
    private val underTest: CurrencyContract.Presenter = CurrencyPresenter(getCurrencyinteractor)

    @Test
    fun `get currency from repository on load`() {
        val view = mock<CurrencyContract.View>()
        val base = "USD"

        whenever(getCurrencyinteractor.getCurrency(any(), any()))
            .thenReturn(Observable.just(mock()))

        underTest.attach(view)
        underTest.load(base)

        verify(getCurrencyinteractor).getCurrency(eq(base), eq(1.0))
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
        verify(view).showCurrency(eq(currency), eq(1.0), eq("1.0"))
    }

    @Test
    fun `load new currency on rates changed by typing`() {
        val view = mock<CurrencyContract.View>()
        val base = "RUB"
        val typedAmount = "156"
        val rates = listOf(Rate("EUR", 66.6))
        val currency = Currency(
            base,
            rates
        )
        val currencyObs = Observable.just(currency)

        whenever(getCurrencyinteractor.getCurrency(any(), any())).thenReturn(currencyObs)

        underTest.attach(view)
        underTest.ratesChanged(base, typedAmount)

        verify(view).showProgress()
        verify(view, never()).showError(any())
        verify(view).showCurrency(eq(currency), eq(156.0), eq(typedAmount))
    }

    @Test
    fun `change typed amount to empty if value is not valid`() {
        val view = mock<CurrencyContract.View>()
        val base = "RUB"
        val brokenAmount = "#4dsdf"
        val typedAmount = ""
        val rates = listOf(Rate("EUR", 66.6))
        val currency = Currency(
            base,
            rates
        )
        val currencyObs = Observable.just(currency)

        whenever(getCurrencyinteractor.getCurrency(any(), any())).thenReturn(currencyObs)

        underTest.attach(view)
        underTest.ratesChanged(base, brokenAmount)

        verify(view).showProgress()
        verify(view, never()).showError(any())
        verify(view).showCurrency(eq(currency), eq(0.0), eq(typedAmount))
    }
}

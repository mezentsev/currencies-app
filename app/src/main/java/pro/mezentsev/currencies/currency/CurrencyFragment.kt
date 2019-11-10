package pro.mezentsev.currencies.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_list.view.*
import pro.mezentsev.currencies.CurrencyApp
import pro.mezentsev.currencies.R
import pro.mezentsev.currencies.base.BaseFragment
import pro.mezentsev.currencies.currency.adapter.CurrencyAdapter
import pro.mezentsev.currencies.currency.adapter.RatesListener
import pro.mezentsev.currencies.di.component.CurrencyComponent.Companion.BASE_CURRENCY
import pro.mezentsev.currencies.di.component.CurrencyComponent.Companion.TYPED_AMOUNT
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate
import javax.inject.Inject
import javax.inject.Named

class CurrencyFragment : BaseFragment(), CurrencyContract.View {
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var presenter: CurrencyPresenter

    @Inject
    @Named(BASE_CURRENCY)
    lateinit var base: String

    @Inject
    @Named(TYPED_AMOUNT)
    lateinit var typedAmount: String

    private val onItemRangeMoved = object :
        RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            recyclerView.smoothScrollBy(0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencyAdapter = CurrencyAdapter()
        CurrencyApp.component
            .currencyFactory()
            .create(
                savedInstanceState?.getString(BUNDLE_BASE_KEY) ?: "EUR",
                savedInstanceState?.getString(BUNDLE_TYPED_AMOUNT) ?: "1.0"
            )
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.list.apply {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(context)
            isFocusable = false
            isFocusableInTouchMode = false
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        currencyAdapter.setRatesListener(object : RatesListener {
            override fun onRatesChanged(rate: Rate) {
                this@CurrencyFragment.base = rate.base
                this@CurrencyFragment.typedAmount = rate.typedValue
                presenter.ratesChanged(rate)
            }
        })
        currencyAdapter.registerAdapterDataObserver(onItemRangeMoved)
        presenter.attach(this)
        presenter.load(base, typedAmount)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_BASE_KEY, base)
        outState.putString(BUNDLE_TYPED_AMOUNT, typedAmount)
    }

    override fun onPause() {
        super.onPause()
        currencyAdapter.unregisterAdapterDataObserver(onItemRangeMoved)
        currencyAdapter.setRatesListener(null)
        presenter.detach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            base = it[BUNDLE_BASE_KEY] as String
            typedAmount = it[BUNDLE_TYPED_AMOUNT] as String
        }
    }

    override fun showCurrency(
        currency: Currency,
        typedValue: String
    ) {
        hideProgress()
        base = currency.base
        typedAmount = typedValue
        currencyAdapter.setCurrency(currency)
    }

    override fun showProgress() {
        view?.let {
            it.progress_view.visibility = VISIBLE
        }
    }

    private fun hideProgress() {
        view?.let {
            it.progress_view.visibility = GONE
        }
    }

    override fun showError(base: String) {
        hideProgress()
        view?.let {
            Snackbar
                .make(
                    it,
                    String.format(resources.getString(R.string.error_loading_currency), base),
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction(R.string.error_loading_reload_action) { presenter.load(this.base, typedAmount) }
                .show()
        }
    }

    companion object {
        private const val BUNDLE_BASE_KEY = "BASE"
        private const val BUNDLE_TYPED_AMOUNT = "TYPED_AMOUNT"

        @JvmStatic
        fun newInstance() = CurrencyFragment()
    }
}

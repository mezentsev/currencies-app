package pro.mezentsev.currencies.currency.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_view.view.*
import pro.mezentsev.currencies.R
import pro.mezentsev.currencies.model.Currency
import pro.mezentsev.currencies.model.Rate

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.RatesHolder>() {

    private var currency: Currency? = null
    private val holderClickListener = HolderListenerHolder()
    private var amount: String? = null

    private var ratesListener: RatesListener? = null

    fun setCurrency(
        currency: Currency,
        typedValue: String
    ) {
        this.amount = typedValue

        val newRates = mutableListOf(Rate(currency.base, typedValue))
        newRates.addAll(currency.rates)

        notifyChanges(currency.base, this.currency?.rates, newRates)
    }

    fun setRatesListener(ratesListener: RatesListener?) {
        this.ratesListener = ratesListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesHolder {
        return RatesHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.currency_view, parent, false)
        )
    }

    override fun getItemCount() = currency?.rates?.size ?: 0

    override fun onBindViewHolder(holder: RatesHolder, position: Int) {
        currency?.rates?.let {
            if (position < it.size) {
                holder.bind(it[position], holderClickListener)
            }
        }
    }

    private fun notifyChanges(
        base: String,
        oldRates: List<Rate>?,
        newRates: List<Rate>
    ) {
        currency = Currency(base, newRates)
        if (oldRates == null) {
            notifyDataSetChanged()
            return
        }

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldRates[oldItemPosition].base == newRates[newItemPosition].base
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldRates[oldItemPosition] == newRates[newItemPosition]
            }

            override fun getOldListSize() = oldRates.size

            override fun getNewListSize() = newRates.size
        })
        diff.dispatchUpdatesTo(this@CurrencyAdapter)
    }

    private inner class HolderListenerHolder : HolderRatesListener {
        override fun onClicked(rate: Rate) {
            ratesListener?.onClicked(rate)
        }

        override fun onTypedRate(rate: Rate) {
            ratesListener?.onRatesChanged(rate)
        }
    }

    class RatesHolder constructor(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, TextWatcher {
        private val imageTxtView = view.currency_image_text
        private val titleTextView = view.currency_title
        private val descriptionTextView = view.currency_description
        private val editCurrencyView = view.currency_edit

        private var holderRatesListener: HolderRatesListener? = null

        private var rate: Rate? = null

        fun bind(rate: Rate, listenerHolder: HolderRatesListener) {
            imageTxtView.text = rate.base
            titleTextView.text = rate.base
            descriptionTextView.text = rate.base

            val rateText = rate.typedValue
            editCurrencyView.setText(rateText)
            editCurrencyView.setSelection(rateText.length)

            this.rate = rate
            this.holderRatesListener = listenerHolder

            editCurrencyView.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && adapterPosition != 0) {
                    onHolderProceed()
                }
            }
            editCurrencyView.addTextChangedListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (adapterPosition != 0) {
                onHolderProceed()
            }
        }

        private fun onHolderProceed() {
            editCurrencyView.requestFocus()

            val currentBase = rate?.base
            currentBase ?: return

            holderRatesListener?.onClicked(Rate(currentBase, editCurrencyView.text.toString()))
        }

        override fun afterTextChanged(s: Editable?) {
            // noop
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // noop
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!editCurrencyView.isFocused) {
                return
            }

            val currentBase = rate?.base
            currentBase ?: return

            holderRatesListener?.onTypedRate(Rate(currentBase, editCurrencyView.text.toString()))
        }
    }

    interface HolderRatesListener {
        fun onClicked(rate: Rate)

        fun onTypedRate(rate: Rate)
    }
}
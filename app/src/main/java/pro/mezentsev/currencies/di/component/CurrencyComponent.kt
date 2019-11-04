package pro.mezentsev.currencies.di.component

import dagger.BindsInstance
import dagger.Subcomponent
import pro.mezentsev.currencies.currency.CurrencyFragment
import pro.mezentsev.currencies.di.scope.PerCurrency
import javax.inject.Named

@PerCurrency
@Subcomponent
interface CurrencyComponent {
    companion object {
        const val BASE_CURRENCY = "BASE_CURRENCY"
        const val TYPED_AMOUNT = "TYPED_AMOUNT"
    }

    fun inject(fragment: CurrencyFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance @Named(BASE_CURRENCY) base: String,
                   @BindsInstance @Named(TYPED_AMOUNT) typedAmount: String): CurrencyComponent
    }
}
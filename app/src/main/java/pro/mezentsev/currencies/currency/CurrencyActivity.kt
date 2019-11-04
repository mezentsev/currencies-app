package pro.mezentsev.currencies.currency

import android.os.Bundle
import pro.mezentsev.currencies.R
import pro.mezentsev.currencies.base.BaseActivity

class CurrencyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        setupActionBar(R.id.toolbar) {
            setTitle(R.string.currency_activity_title)
        }

        if (savedInstanceState == null) {
            replaceFragment(R.id.frame, CurrencyFragment.newInstance())
        }
    }
}

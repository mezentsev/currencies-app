package pro.mezentsev.currencies.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("latest")
    fun getLatest(@Query("base") base: String): Observable<CurrencyResponse>

}
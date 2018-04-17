package uk.co.fanduel.highpoints.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.fanduel.highpoints.model.Player

class PlayersApi(private val baseUrl: String) {

    // TODO:
    // - Consider passing in retrofit when more APIs are added
    // - Consider passing in playersService so that it can be mocked in tests
    // - Consider using Dagger to inject dependencies
    // - Consider just returning list of players, not wrapper object

    private val gson: Gson by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }

    private val playersService: PlayersService by lazy {
        retrofit.create(PlayersService::class.java)
    }

    fun getPlayers(): Observable<List<Player>> = playersService.getPlayers().map { stats -> stats.players }
}
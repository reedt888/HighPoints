package uk.co.fanduel.highpoints.api

import io.reactivex.Observable
import retrofit2.http.GET
import uk.co.fanduel.highpoints.model.Players

interface PlayersService {

    @GET("liamjdouglas/bb40ee8721f1a9313c22c6ea0851a105/raw/6b6fc89d55ebe4d9b05c1469349af33651d7e7f1/Player.json")
    fun getPlayers(): Observable<Players>
}
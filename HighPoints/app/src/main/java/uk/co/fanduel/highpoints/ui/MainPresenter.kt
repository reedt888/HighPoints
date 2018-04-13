package uk.co.fanduel.highpoints.ui

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.fanduel.highpoints.api.PlayersApi

class MainPresenter(private val playersApi: PlayersApi, private val mainView: MainView) {

    // TODO: Consider moving schedulers code to a separate object to remove Android from presenter

    private var disposable: Disposable? = null

    fun start() {
        disposable = playersApi.getPlayers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mainView.showPlayers(it)
            }, {
                mainView.showLoadPlayersError()
            })
    }

    fun stop() {
        disposable?.dispose()
    }
}
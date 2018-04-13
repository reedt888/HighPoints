package uk.co.fanduel.highpoints.ui

import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.model.Players

class MainPresenterTest {

    private val playersApi = Mockito.mock(PlayersApi::class.java)
    private val view = Mockito.mock(MainView::class.java)
    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        presenter = MainPresenter(playersApi, view)
    }

    @Test
    fun whenStartAndGetPlayersSucceedsThenViewShowsPlayers() {
        val expectedPlayers = Players(emptyList())
        Mockito.`when`(playersApi.getPlayers()).thenReturn(Observable.just(expectedPlayers))
        presenter.start()
        Mockito.verify(view).showPlayers(expectedPlayers)
    }

    @Test
    fun whenStartAndGetPlayersFailsThenViewShowsError() {
        Mockito.`when`(playersApi.getPlayers()).thenReturn(Observable.error(Throwable()))
        presenter.start()
        Mockito.verify(view).showLoadPlayersError()
    }

    @Test
    fun whenStopThenModelAndViewUnaffected() {
        presenter.stop()
        Mockito.verifyNoMoreInteractions(playersApi)
        Mockito.verifyNoMoreInteractions(view)
    }
}
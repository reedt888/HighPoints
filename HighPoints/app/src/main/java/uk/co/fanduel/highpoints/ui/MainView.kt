package uk.co.fanduel.highpoints.ui

import uk.co.fanduel.highpoints.model.Players

interface MainView {

    fun showPlayers(playersResponse: Players)

    fun showLoadPlayersError()
}
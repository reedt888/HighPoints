package uk.co.fanduel.highpoints.ui

import uk.co.fanduel.highpoints.model.Players

interface MainView {

    fun showPlayers(players: Players)

    fun showLoadPlayersError()
}
package uk.co.fanduel.highpoints.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import uk.co.fanduel.highpoints.R
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.model.Players

class MainFragment : Fragment(), MainView {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        presenter = MainPresenter(PlayersApi(getString(R.string.base_url)), this)
        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun showPlayers(players: Players) {
        Toast.makeText(context, Integer.toString(players.players.size), Toast.LENGTH_SHORT).show()
    }

    override fun showLoadPlayersError() {
        Toast.makeText(context, R.string.error_load_players, Toast.LENGTH_SHORT).show()
    }
}
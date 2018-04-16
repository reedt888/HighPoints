package uk.co.fanduel.highpoints.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_main.*
import uk.co.fanduel.highpoints.R
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.model.Player

class MainFragment : Fragment(), MainView {

    // TODO:
    // - Consider injecting the presenter so we can mock it and test the fragment using Robolectric
    // - Would also enable basic Robolectric testing of the parent activity (although not much to test)
    // - Consider adding placeholder and error drawables for loading of player images
    // - Consider using a Snackbar with a retry option when player loading fails
    // - Retain state on configuration change

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

        presenter = MainPresenter(this, PlayersApi(getString(R.string.base_url)))
        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun showOptions(options: Pair<Player, Player>) {
        with (options) {
            displayPlayer(options.first, player_1_image, player_1_name)
            displayPlayer(options.second, player_2_image, player_2_name)
        }
    }

    override fun scheduleNext() {
        // TODO
    }

    override fun showSelectedCorrect(selected: Player) {
        // TODO
    }

    override fun showSelectedIncorrect(selected: Player) {
        // TODO
    }

    override fun showCorrectSoFar(correctSoFar: Int) {
        // TODO
    }

    override fun showComplete() {
        // TODO
    }

    override fun showNotEnoughPlayersError() {
        Toast.makeText(context, R.string.error_not_enough_players, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadPlayersError() {
        Toast.makeText(context, R.string.error_load_players, Toast.LENGTH_SHORT).show()
    }

    private fun displayPlayer(player: Player, imageView: ImageView, nameView: TextView) {
        Picasso.get().load(player.images.default.url).into(imageView)
        nameView.text = player.firstName
    }
}
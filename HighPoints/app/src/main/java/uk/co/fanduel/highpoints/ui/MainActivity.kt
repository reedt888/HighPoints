package uk.co.fanduel.highpoints.ui

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.fanduel.highpoints.R
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.game.GameState
import uk.co.fanduel.highpoints.model.Player
import java.text.NumberFormat

class MainActivity : AppCompatActivity(), MainView {

    // TODO:
    // - Consider injecting the presenter so we can mock it and test the fragment using Robolectric
    // - Consider adding placeholder and error drawables for loading of player images
    // - Consider using a Snackbar with a retry option when player loading fails
    // - Use Robolectric to unit test methods

    private companion object {
        private const val scheduleNextDelay = 1000L
    }

    private lateinit var presenter: MainPresenter
    private var selectedImage: ImageView? = null
    private var selectedScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configurePlayer1Card()
        configurePlayer2Card()
        configureReset()
        configurePresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun showOptions(options: Pair<Player, Player>) {
        with(options) {
            displayPlayer(options.first, player_1_card, player_1_name, player_1_image, player_1_score)
            displayPlayer(options.second, player_2_card, player_2_name, player_2_image, player_2_score)
        }

        player_1_card.isClickable = true
        player_2_card.isClickable = true
    }

    override fun scheduleNext() {
        Handler().postDelayed({ presenter.onNext() }, scheduleNextDelay)
    }

    override fun showSelectedCorrect(selected: Player) {
        selectedImage?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_correct))
        selectedScore?.text = NumberFormat.getInstance().format(selected.fppg)
    }

    override fun showSelectedIncorrect(selected: Player) {
        selectedImage?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_incorrect))
        selectedScore?.text = NumberFormat.getInstance().format(selected.fppg)
    }

    override fun showCorrectSoFar(correctSoFar: Int) {
        correct_count.text = correctSoFar.toString()
        target_count.text = GameState.targetCorrect.toString()
    }

    override fun showComplete() {
        player_1_card.isClickable = false
        player_2_card.isClickable = false
        Toast.makeText(this, R.string.game_complete_message, Toast.LENGTH_LONG).show()
    }

    override fun showNotEnoughPlayersError() {
        Toast.makeText(this, R.string.error_not_enough_players, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadPlayersError() {
        Toast.makeText(this, R.string.error_load_players, Toast.LENGTH_SHORT).show()
    }

    private fun configurePlayer1Card() {
        player_1_card.setOnClickListener {
            player_1_card.isClickable = false
            selectedImage = player_1_image
            selectedScore = player_1_score
            presenter.onPlayerSelected(player_1_card.tag as Player)
        }
    }

    private fun configurePlayer2Card() {
        player_2_card.setOnClickListener {
            player_2_card.isClickable = false
            selectedImage = player_2_image
            selectedScore = player_2_score
            presenter.onPlayerSelected(player_2_card.tag as Player)
        }
    }

    private fun configureReset() {
        reset_button.setOnClickListener {
            player_1_card.isClickable = false
            player_2_card.isClickable = false
            selectedImage = null
            selectedScore = null
            presenter.onReset()
        }
    }

    private fun configurePresenter() {
        presenter = MainPresenter(this, PlayersApi(getString(R.string.base_url)))
        presenter.start()
    }

    private fun displayPlayer(
        player: Player,
        card: CardView,
        nameView: TextView,
        imageView: ImageView,
        scoreView: TextView
    ) {
        card.tag = player
        Picasso.get().load(player.images.default.url).into(imageView)
        nameView.text = player.firstName
        scoreView.text = null
    }
}

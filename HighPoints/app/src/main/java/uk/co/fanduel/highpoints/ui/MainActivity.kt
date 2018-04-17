package uk.co.fanduel.highpoints.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.fanduel.highpoints.R
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.game.GamePrefsPersister
import uk.co.fanduel.highpoints.game.GameState
import uk.co.fanduel.highpoints.model.Player
import java.text.NumberFormat

class MainActivity : AppCompatActivity(), MainView {

    // TODO:
    // - Consider injecting the presenter so we can mock it and test the fragment using Robolectric
    // - Consider adding placeholder and error drawables for loading of player images
    // - Consider using a Snackbar with a retry option when player loading fails
    // - Make MainView methods more granular, e.g. showOptions() also makes views non-clickable
    // - Display a progress indicator while loading players
    // - Use Robolectric to unit test methods

    private companion object {
        private const val scheduleNextDelay = 750L
        private const val showCompleteDelay = 1000L
    }

    private lateinit var presenter: MainPresenter
    private var selectedImage: ImageView? = null
    private var selectedScore: TextView? = null
    private var handler = Handler()

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

    @SuppressLint("InflateParams")
    override fun showInstructions() {
        AlertDialog
            .Builder(this)
            .setView(layoutInflater.inflate(R.layout.dialog_instructions, null))
            .setPositiveButton(android.R.string.ok, { dialogInterface, _ ->
                dialogInterface.dismiss()
                presenter.onInstructionsAcknowledged()
            })
            .setCancelable(false)
            .create()
            .show()
    }

    override fun showInitialOptions(options: Pair<Player, Player>) {
        main_container.visibility = View.VISIBLE
        displayOptions(options)
    }

    override fun showNextOptions(options: Pair<Player, Player>) {
        val outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
        val inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)

        outAnimation.setAnimationListener(object: DefaultAnimationListener() {
            override fun onAnimationEnd(p0: Animation?) {
                displayOptions(options)
                players_container.startAnimation(inAnimation)
            }
        })
        players_container.startAnimation(outAnimation)
    }

    override fun scheduleNext() {
        handler.postDelayed({ presenter.onNext() }, scheduleNextDelay)
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

    @SuppressLint("InflateParams")
    override fun showComplete() {
        player_1_card.isClickable = false
        player_2_card.isClickable = false

        handler.postDelayed({
            AlertDialog
                .Builder(this)
                .setView(layoutInflater.inflate(R.layout.dialog_complete, null))
                .setPositiveButton(android.R.string.ok, { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                .setCancelable(false)
                .create()
                .show()
        }, showCompleteDelay)
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
        presenter = MainPresenter(
            this,
            GamePrefsPersister(this),
            PlayersApi(getString(R.string.base_url))
        )
        presenter.start()
    }

    private fun displayOptions(options: Pair<Player, Player>) {
        with(options) {
            displayPlayer(
                options.first,
                player_1_card,
                player_1_name,
                player_1_image,
                player_1_score
            )
            displayPlayer(
                options.second,
                player_2_card,
                player_2_name,
                player_2_image,
                player_2_score
            )
        }

        player_1_card.isClickable = true
        player_2_card.isClickable = true
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

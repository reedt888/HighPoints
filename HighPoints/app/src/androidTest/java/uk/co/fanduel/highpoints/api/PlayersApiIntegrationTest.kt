package uk.co.fanduel.highpoints.api

import android.support.test.InstrumentationRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.Test
import uk.co.fanduel.highpoints.R

class PlayersApiIntegrationTest {

    @Test
    fun whenGetPlayersThenCorrectNumberAndFirstPlayerReturned() {
        val baseUrl = InstrumentationRegistry.getTargetContext().getString(R.string.base_url)
        val playersApi = PlayersApi(baseUrl)
        val players = playersApi.getPlayers().blockingFirst()

        assertThat(players.size, equalTo(59))

        with(players.first()) {
            assertThat(id, equalTo("15475-9524"))
            assertThat(firstName, equalTo("Stephen"))
            assertThat(fppg, equalTo(47.94303797468354))
            assertThat(
                images.default.url,
                equalTo("https://d17odppiik753x.cloudfront.net/playerimages/nba/9524.png")
            )
        }
    }

    @Test
    fun whenGetPlayersThenAllPropertiesPopulatedForAllPlayers() {
        val baseUrl = InstrumentationRegistry.getTargetContext().getString(R.string.base_url)
        val playersApi = PlayersApi(baseUrl)
        val players = playersApi.getPlayers().blockingFirst()

        for (player in players) {
            with(player) {
                assertThat(id, `is`(notNullValue()))
                assertThat(firstName, `is`(notNullValue()))
                assertThat(fppg, `is`(notNullValue()))
                assertThat(images.default.url, `is`(notNullValue()))
            }
        }
    }
}
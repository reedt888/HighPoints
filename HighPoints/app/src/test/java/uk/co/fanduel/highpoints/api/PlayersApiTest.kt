package uk.co.fanduel.highpoints.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class PlayersApiTest {

    @Test
    fun whenGetPlayersThenPlayersTomAndDickReturned() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("{\"players\":[{\"first_name\":\"Tom\",\"fppg\":1.23,\"images\":{\"default\":{\"url\": \"URL1\"}}},{\"first_name\":\"Dick\",\"fppg\":4.56,\"images\":{\"default\":{\"url\": \"URL2\"}}}]}"))
        server.start()

        val baseUrl = server.url("").url().toString()
        val playersApi = PlayersApi(baseUrl)
        val playersResponse = playersApi.getPlayers().blockingFirst()

        with(playersResponse) {

            assertThat(players.size, equalTo(2))

            with(players[0]) {
                assertThat(firstName, equalTo("Tom"))
                assertThat(fppg, equalTo(1.23))
                assertThat(images.default.url, equalTo("URL1"))
            }

            with(players[1]) {
                assertThat(firstName, equalTo("Dick"))
                assertThat(fppg, equalTo(4.56))
                assertThat(images.default.url, equalTo("URL2"))
            }
        }

        server.shutdown()
    }
}
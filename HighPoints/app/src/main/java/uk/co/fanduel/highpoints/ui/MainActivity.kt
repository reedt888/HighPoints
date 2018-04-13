package uk.co.fanduel.highpoints.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import uk.co.fanduel.highpoints.R

class MainActivity : AppCompatActivity() {

    companion object {
        private const val fragmentTag = "MainFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = MainFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, fragmentTag).commit()
        }
    }
}

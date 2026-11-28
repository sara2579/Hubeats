package com.hubeats.app.ui.student

// Android basics (built into every phone)
import android.content.Intent                 // a "message" to open another screen (used in logOut)
import android.os.Bundle                      // saved data passed into onCreate
import android.widget.Toast                   // small pop-up message (used in openAbout; delete in Stage 6)

// AppCompat (makes newer features work on older phones)
import androidx.appcompat.app.AppCompatActivity  // the base class every activity builds on

// Drawer
import androidx.drawerlayout.widget.DrawerLayout // the layout that lets the drawer slide in

// Navigation Component (swaps screens using nav_student.xml)
import androidx.navigation.NavController                       // the "driver" that swaps screens
import androidx.navigation.fragment.NavHostFragment            // the box where screens appear; owns the NavController
import androidx.navigation.ui.AppBarConfiguration              // which screens show ☰ (top level) vs ← (inner)
import androidx.navigation.ui.NavigationUI                     // helper: opens a screen when its drawer item is tapped
import androidx.navigation.ui.navigateUp                       // ☰/← button: open drawer or go back
import androidx.navigation.ui.setupActionBarWithNavController  // links toolbar title + ☰/← to navigation
import androidx.navigation.ui.setupWithNavController           // links drawer to navigation (highlights current screen)


// Material Design (Google's ready-made UI pieces)
import com.google.android.material.appbar.MaterialToolbar         // the top bar
import com.google.android.material.navigation.NavigationView      // the panel inside the drawer showing the menu

// Our own project
import com.hubeats.app.R                      // auto-generated list of every id, layout and string in res
import com.hubeats.app.ui.auth.LoginActivity  // login screen (opened on log out)
import com.hubeats.app.util.ThemeHelper       // Zuni's: saves and applies the dark mode choice

//temporarily for the temporray psarts added os login works while salam finishes her login
import androidx.lifecycle.lifecycleScope
import com.hubeats.app.data.database.AppDatabase
import com.hubeats.app.data.repository.HubEatsRepository
import com.hubeats.app.util.SessionManager
import kotlinx.coroutines.launch

// The frame around all student screens: toolbar + drawer + navigation. (GAMAR)
class StudentActivity : AppCompatActivity() {

    // "lateinit" = we will fill these in onCreate, before using them
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // onCreate runs once when the screen opens
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply the light/dark choice saved last time (Zuni's ThemeHelper),
        // BEFORE the screen is drawn, so it opens in the right colours
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)   // show the layout

        // 1. Use our MaterialToolbar as this screen's top bar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Find the drawer and the list inside it
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        // 3. Get the navController: the object that swaps screens using nav_student.xml
        // we need the driver (navController), but it lives inside the box (NavHostFragment), and we find the box through the fragment manager.
        // then Save it in the lateinit var navController declared at the top of the class

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 4. configuring the bar . "Top-level(coz starting points)" screens show the ☰ button. Other screens like cart and order details show a ← back arrow.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.restaurantListFragment, R.id.myOrdersFragment),
            drawerLayout
            //drawerLayout	Tells it which drawer the ☰ button should , from line 51
        )

        // 5. Connect the toolbar and drawer to navigation:
        //    - the toolbar title changes to each screen's label
        //    - the drawer highlights the screen you're on
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //setupActionBar line -"Toolbar, follow the controller. Every time the screen changes, show its name, and show ☰ or ← using the rule from step 4."
        // navView line -"Drawer, follow the controller too. Every time the screen changes, highlight the matching item.


        // 6. Decide what happens when a drawer item is tapped
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_dark_mode -> toggleDarkMode() //all methods below
                R.id.action_about -> openAbout()
                R.id.action_log_out -> logOut()
                // Restaurants = the first screen. Go back to it, removing the screens on top of it.
                // (false = keep the restaurant list itself.) We do this ourselves because the
                // automatic version can bring My Orders straight back after an order is placed.
                R.id.restaurantListFragment -> navController.popBackStack(R.id.restaurantListFragment, false)
                // Anything else is a screen (My Orders): let navigation open it
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
            drawerLayout.closeDrawers()   // slide the drawer shut
            true
            //a block like { item -> ... } expects a boolean answer from the last line.
            // There's no return word here. so we keep our last line as true as in we handled the tap
            // so that  answer is sent back to Android.
        }


        // TEMPORARY (until Salam's login works): fill the database with sample data
        // and pretend "student1" is logged in. DELETE this block when login is done.
        lifecycleScope.launch {   // runs in the background (coroutine), so the screen doesn't freeze
            val repository = HubEatsRepository(AppDatabase.getDatabase(applicationContext))
            repository.addSampleDataIfEmpty()                 // adds restaurants, menus, accounts (only the first time)
            val user = repository.login("student1", "1234")   // find the test student
            if (user != null) {
                SessionManager(this@StudentActivity)
                    .saveUser(user.id, user.fullName, user.role, user.restaurantId)  // save as "logged in"
            }
        }

    }

    // Called when the ☰ or ← in the toolbar is tapped
    override fun onSupportNavigateUp(): Boolean {
        // Open the drawer if main screens, or go back if other screens
        return navController.navigateUp(appBarConfiguration)
    }
    //asks the navController to handle the tap using the rule from step 4:
    //main screen (My Orders / Restaurants) → open the drawer
    //other screen (Order details, etc.) → go back one screen
    //It answers true if it did something.


    // Switches between light and dark mode
    private fun toggleDarkMode() {
        // Zuni's ThemeHelper does it all: flips the choice, SAVES it (so it's remembered
        // next time the app opens), and applies it. Android then redraws the screen in the new colours.
        ThemeHelper.toggle(this)
    }

    // Opens the About screen
    private fun openAbout() {
        // TODO (Stage 6): when Salam's AboutActivity exists, replace the Toast with:
        // startActivity(Intent(this, AboutActivity::class.java))
        Toast.makeText(this, "About screen coming soon", Toast.LENGTH_SHORT).show()
    }

    // Goes back to the login screen
    private fun logOut() {
        // TODO (Stage 6): first clear the saved user with Salam's SessionManager

        val intent = Intent(this, LoginActivity::class.java)
        // These flags wipe the screen history, so "back" can't return to the student screens
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()   // close this activity
    }
}
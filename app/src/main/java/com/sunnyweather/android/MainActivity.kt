package com.sunnyweather.android

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_menu_black_24)
            it.setDisplayShowTitleEnabled(false)
        }
        //颜色主题
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val dayNight = sharedPreferences.getBoolean("dayNight", false)
        if (dayNight) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //底部导航栏
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
        //标题栏的标题click事件
        main_toolBar_title.setOnClickListener {
//            val dialog: CustomBottomDialog = CustomBottomDialog(this)
//            dialog.show()
        }
        //搜索栏
//        search_home.setIconifiedByDefault(false)
//        search_home.isSubmitButtonEnabled = true
//        search_home.queryHint = "搜索主播"
//        search_home.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
//            .setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
//        search_home.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                val intent = Intent(context, SearchActivity::class.java).apply {
//                    putExtra("query", query)
//                }
//                startActivity(intent)
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> main_drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }
}
package com.sunnyweather.android

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sunnyweather.android.ui.area.AreaFragment
import com.sunnyweather.android.ui.area.AreaSingleFragment
import kotlinx.android.synthetic.main.activity_main.*
import top.limuyang2.customldialog.BottomTextListDialog

class MainActivity : AppCompatActivity(), AreaSingleFragment.FragmentListener {
    private lateinit var areaFragment: AreaFragment

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
        navController.addOnDestinationChangedListener { _, destination, _ ->
        if(destination.id == R.id.navigation_home) {
            val title = SunnyWeatherApplication.areaName.value
            if (title == "all" || title == null) {
                main_toolBar_title.text = "全部推荐"
            } else {
                main_toolBar_title.text = title
            }
            val drawable = resources.getDrawable(R.drawable.baseline_arrow_drop_down_black_24)
            main_toolBar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else if(destination.id == R.id.navigation_follow) {
            main_toolBar_title.text = "关注"
            main_toolBar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            main_toolBar_title.text = "分区"
            main_toolBar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }}
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
        //标题栏的标题click事件
        main_toolBar_title.setOnClickListener {
            val fragmentManager = supportFragmentManager
            areaFragment = AreaFragment()
            areaFragment.show(fragmentManager, "areaFragment")
        }
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

    override fun onFragment(areaType: String, areaName:String) {
        main_toolBar_title.text = areaName
        SunnyWeatherApplication.areaType.value = areaType
        SunnyWeatherApplication.areaName.value = areaName
        areaFragment.dismiss()
    }
}
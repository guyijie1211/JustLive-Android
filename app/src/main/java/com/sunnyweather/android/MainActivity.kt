package com.sunnyweather.android

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.android.ui.area.AreaFragment
import com.sunnyweather.android.ui.area.AreaSingleFragment
import com.sunnyweather.android.ui.follows.FollowsFragment
import com.sunnyweather.android.ui.home.HomeFragment
import com.sunnyweather.android.ui.login.LoginActivity
import com.sunnyweather.android.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity(), AreaSingleFragment.FragmentListener {
    private lateinit var areaFragment: AreaFragment
    private lateinit var viewPager: ViewPager2
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
        //ViewPager2
        viewPager = main_fragment
        viewPager.isUserInputEnabled = false
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        val title = SunnyWeatherApplication.areaName.value
                        if (title == "all" || title == null) {
                            main_toolBar_title.text = "全部推荐"
                        } else {
                            main_toolBar_title.text = title
                        }
                        val drawable = resources.getDrawable(R.drawable.baseline_arrow_drop_down_black_24)
                        main_toolBar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    }
                    1 -> {
                        main_toolBar_title.text = "关注"
                        main_toolBar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
            }
        })
        //tabLayout
        ViewPager2Delegate.install(viewPager, tab_main)
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> main_drawerLayout.openDrawer(GravityCompat.START)
            R.id.menu_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.toolbar_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onFragment(areaType: String, areaName:String) {
        main_toolBar_title.text = areaName
        SunnyWeatherApplication.areaType.value = areaType
        SunnyWeatherApplication.areaName.value = areaName
        areaFragment.dismiss()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> HomeFragment()
                1 -> FollowsFragment()
                else -> Fragment()
            }
    }

    fun toFirst(){
        viewPager.currentItem = 0
    }
}
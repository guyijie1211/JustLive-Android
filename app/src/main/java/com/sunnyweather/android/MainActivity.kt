package com.sunnyweather.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.ui.area.AreaFragment
import com.sunnyweather.android.ui.area.AreaSingleFragment
import com.sunnyweather.android.ui.follows.FollowsFragment
import com.sunnyweather.android.ui.home.HomeFragment
import com.sunnyweather.android.ui.login.LoginViewModel
import com.sunnyweather.android.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*

import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blankj.utilcode.util.AppUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.sunnyweather.android.logic.model.UpdateInfo
import com.sunnyweather.android.logic.model.UpdateResponse
import com.sunnyweather.android.logic.network.LiveService
import com.sunnyweather.android.logic.network.ServiceCreator
import com.sunnyweather.android.ui.login.LoginActivity
import com.sunnyweather.android.ui.setting.SettingActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.dialog_update.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), AreaSingleFragment.FragmentListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private lateinit var areaFragment: AreaFragment
    private lateinit var viewPager: ViewPager2
    private var isVersionCheck = false
    private lateinit var mMenu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolBar)
        initLogin()
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_menu_black_24)
            it.setDisplayShowTitleEnabled(false)
        }
        //关闭抽屉滑动打开
        main_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //if you want to update the items at a later time it is recommended to keep it in a variable
        val item1 = PrimaryDrawerItem().apply { nameRes = R.string.drawItem1; identifier = 1; isSelectable = false }
        val item2 = SecondaryDrawerItem().apply { nameRes = R.string.drawItem2; identifier = 2; isSelectable = false }
        AccountHeaderView(this).apply {
            attachToSliderView(slider) // attach to the slider
            addProfiles(
                ProfileDrawerItem().apply { nameText = "Mike Penz"; descriptionText = "mikepenz@gmail.com"; identifier = 102 }
            )
            withSavedInstance(savedInstanceState)
        }
// get the reference to the slider and add the items
        slider.itemAdapter.add(
            item1,
            DividerDrawerItem(),
            item2,
            SecondaryDrawerItem().apply { nameRes = R.string.drawItem3 }
        )

// specify a click listener
        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            // do something with the clicked item :D
            false
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
        viewModel.updateResponseLiveData.observe(this, { result ->
            val updateInfo = result.getOrNull()
            if (updateInfo is UpdateInfo) {
                var sharedPref = getSharedPreferences("JustLive", Context.MODE_PRIVATE)
                val ignoreVersion = sharedPref.getInt("ignoreVersion",0)
                if (ignoreVersion == updateInfo.versionNum && !isVersionCheck) return@observe
                var descriptions = ""
                var index = 1
                for (item in updateInfo.description) {
                    descriptions = "$descriptions$index.$item<br>"
                    index++
                }
                val dialogContent = Html.fromHtml("<div>$descriptions</div>")
                MaterialDialog(this).show {
                    customView(R.layout.dialog_update)
                    update_description.text = dialogContent
                    update_version.text = "版本: ${updateInfo.latestVersion}"
                    update_size.text = "下载体积: ${updateInfo.apkSize}"
                    ignore_btn.setOnClickListener {
                        var sharedPref = context.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
                        sharedPref.edit().putInt("ignoreVersion", updateInfo.versionNum).commit()
                        Toast.makeText(context, "已忽略", Toast.LENGTH_SHORT).show()
                        cancel()
                    }
                    versionchecklib_version_dialog_cancel.setOnClickListener {
                        dismiss()
                    }
                    versionchecklib_version_dialog_commit.setOnClickListener {
                        val uri = Uri.parse(updateInfo.updateUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.addCategory(Intent. CATEGORY_BROWSABLE)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    if (isVersionCheck) {
                        ignore_btn.visibility = View.GONE
                    }
                }
            } else if(updateInfo is String){
                Toast.makeText(this, "用户密码已修改，请重新登录", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
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

        //启动页
        val startPage = sharedPreferences.getString("start_page", "0")
        if (startPage == "1") {
            viewPager.currentItem = 1
        }
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
        Log.i("test", "onCreateOptionsMenu")
        inflater.inflate(R.menu.toolbar, menu)
        mMenu = menu
        SunnyWeatherApplication.isLogin.observe(this, {result ->
            if (result) {
                mMenu.findItem(R.id.toolbar_login).isVisible = false
                mMenu.findItem(R.id.toolbar_logout).isVisible = true
            } else {
                mMenu.findItem(R.id.toolbar_login).isVisible = true
                mMenu.findItem(R.id.toolbar_logout).isVisible = false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            android.R.id.home -> main_drawerLayout.openDrawer(GravityCompat.START)
            android.R.id.home -> {
                Toast.makeText(this, "开发中", Toast.LENGTH_SHORT).show()
            }
            R.id.toolbar_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.toolbar_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.toolbar_update -> {
                isVersionCheck = true
                viewModel.checkVersion()
            }
            R.id.toolbar_logout -> {
                SunnyWeatherApplication.clearLoginInfo(this)
                main_fragment.currentItem = 0
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
    private fun initLogin(){
        var sharedPref = this.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "").toString()
        val password = sharedPref.getString("password", "").toString()
        val hsa = sharedPref.contains("")
        viewModel.loginResponseLiveDate.observe(this, { result ->
            val userInfo = result.getOrNull()
            if (userInfo is UserInfo) {
                MobclickAgent.onProfileSignIn(userInfo.userName)//友盟账号登录
                SunnyWeatherApplication.userInfo = userInfo
                SunnyWeatherApplication.isLogin.value = true
            } else if(userInfo is String){
                SunnyWeatherApplication.clearLoginInfo(this)
                Toast.makeText(this, "用户密码已修改，请重新登录", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        if (password.length > 1) {
            viewModel.doLogin(username, password)
        }
        viewModel.checkVersion()
    }
}
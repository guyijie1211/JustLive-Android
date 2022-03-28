package com.sunnyweather.android

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.*
import com.sunnyweather.android.logic.model.UpdateInfo
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.ui.area.AreaPopup
import com.sunnyweather.android.ui.area.AreaSingleFragment
import com.sunnyweather.android.ui.follows.FollowsFragment
import com.sunnyweather.android.ui.home.HomeFragment
import com.sunnyweather.android.ui.login.LoginActivity
import com.sunnyweather.android.ui.login.LoginViewModel
import com.sunnyweather.android.ui.search.SearchActivity
import com.sunnyweather.android.ui.setting.SettingActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import com.sunnyweather.android.ui.about.AboutActvity
import com.blankj.utilcode.util.ToastUtils

import android.graphics.Bitmap
import com.lxj.xpopup.XPopup
import moe.feng.alipay.zerosdk.AlipayZeroSdk
import kotlinx.android.synthetic.main.dialog_donate.*


class MainActivity : AppCompatActivity(), AreaSingleFragment.FragmentListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private lateinit var areaPopup : AreaPopup
    private lateinit var viewPager: ViewPager2
    private var isVersionCheck = false
    private lateinit var mMenu: Menu
    private var themeActived = R.style.SunnyWeather
    private var autoDark = true
    private var mShortcutManager:ShortcutManager? = null
    private var activityMain = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //颜色主题
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        autoDark = sharedPreferences.getBoolean("autoDark", true)
        if (autoDark) {
            if(SunnyWeatherApplication.isNightMode(this)){
                themeActived = R.style.nightTheme
                sharedPreferences.edit().putInt("theme", themeActived).commit()
            } else {
                themeActived = R.style.SunnyWeather
                sharedPreferences.edit().putInt("theme", themeActived).commit()
            }
        } else {
            themeActived = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        }
        setTheme(themeActived)
        setContentView(R.layout.activity_main)
        drawer_dark_switch.isChecked = (themeActived == R.style.nightTheme)
        BarUtils.addMarginTopEqualStatusBarHeight(drawer_nick)
        BarUtils.transparentStatusBar(this)
        BarUtils.setStatusBarLightMode(this, themeActived != R.style.nightTheme)
        setSupportActionBar(main_toolBar)
        initLogin()
        areaPopup = AreaPopup(this)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.icon_menu)
            it.setDisplayShowTitleEnabled(false)
        }

        //Drawer
        drawer_dark_switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                sharedPreferences.edit().putInt("theme", R.style.nightTheme).commit()
                drawer_dark_switch.isChecked = true
            } else {
                sharedPreferences.edit().putInt("theme", R.style.SunnyWeather).commit()
                drawer_dark_switch.isChecked = false
            }
            recreate()
        }
        drawer_dark_switch.setOnClickListener {
            sharedPreferences.edit().putBoolean("autoDark",false).commit()
        }
        drawer_logout.setOnClickListener {
            SunnyWeatherApplication.clearLoginInfo(this)
            main_fragment.currentItem = 0
        }
        drawer_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        drawer_setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        drawer_support.setOnClickListener {
            MaterialDialog(this).show {
                customView(R.layout.dialog_donate)
                var payPlatform = "zfb"
                donate_cancel.setOnClickListener {
                    dismiss()
                }
                donate_save_cancel.setOnClickListener {
                    donate_contain.visibility = View.VISIBLE
                    donate_pic_contain.visibility = View.GONE
                }
                donate_zfb.setOnClickListener {
                    if (AlipayZeroSdk.hasInstalledAlipayClient(context)) {
                        AlipayZeroSdk.startAlipayClient(activityMain, "fkx19479mrxqi6tzhgw0bd0")
                    } else {
                        payPlatform = "zfb"
                        donate_pic.setImageDrawable(resources.getDrawable(R.drawable.zfb_pic))
                        donate_contain.visibility = View.GONE
                        donate_pic_contain.visibility = View.VISIBLE
                        donate_save_zfb.visibility = View.VISIBLE
                        donate_save_wx.visibility = View.INVISIBLE
                    }
                }
                donate_wx.setOnClickListener {
                    payPlatform = "wx"
                    donate_pic.setImageDrawable(resources.getDrawable(R.drawable.wx_pic))
                    donate_contain.visibility = View.GONE
                    donate_pic_contain.visibility = View.VISIBLE
                    donate_save_zfb.visibility = View.INVISIBLE
                    donate_save_wx.visibility = View.VISIBLE
                }
                donate_save_zfb.setOnClickListener {
                    ImageUtils.save2Album(ImageUtils.drawable2Bitmap(resources.getDrawable(R.drawable.zfb_pic)),Bitmap.CompressFormat.JPEG)
                    ToastUtils.showLong("二维码已保存到相册,请打开支付宝扫码使用")
                }
                donate_save_wx.setOnClickListener {
                    ImageUtils.save2Album(ImageUtils.drawable2Bitmap(resources.getDrawable(R.drawable.wx_pic)),Bitmap.CompressFormat.JPEG)
                    ToastUtils.showLong("二维码已保存到相册,请打开微信扫码使用")
                }
            }
        }
        drawer_report.setOnClickListener {
            val intent = Intent(this, AboutActvity::class.java)
            startActivity(intent)
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
                val versionNum = SunnyWeatherApplication.getVersionCode(SunnyWeatherApplication.context)
                if (versionNum == updateInfo.versionNum || ignoreVersion == updateInfo.versionNum) {
                    if (isVersionCheck) {
                        Toast.makeText(SunnyWeatherApplication.context, "当前已是最新版本^_^", Toast.LENGTH_SHORT).show()
                    }
                    return@observe
                }
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
                        val drawable = getDrawable(R.drawable.icon_arrow_down)
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
            areaPopup = AreaPopup(this)
            XPopup.Builder(this)
                .isDestroyOnDismiss(true)
                .autoFocusEditText(false)
                .moveUpToKeyboard(false)
                .popupHeight(ScreenUtils.getAppScreenHeight() * 4 / 5)
                .isViewMode(true)
                .asCustom(areaPopup)
                .show();
        }

        //动态创建shortcuts
        createDynamicShortcut(themeActived)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val newTheme = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        drawer_dark_switch.isChecked = (themeActived == R.style.nightTheme)
        if (newTheme != themeActived){
            recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu)
        mMenu = menu
        SunnyWeatherApplication.isLogin.observe(this, {result ->
            if (result) {
                drawer_nick.text = SunnyWeatherApplication.userInfo?.nickName
                drawer_username.text = SunnyWeatherApplication.userInfo?.userName
                drawer_logout.visibility = View.VISIBLE
                drawer_login.visibility = View.INVISIBLE
            } else {
                drawer_nick.text = "未登录"
                drawer_username.text = ""
                drawer_logout.visibility = View.INVISIBLE
                drawer_login.visibility = View.VISIBLE
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> main_drawerLayout.openDrawer(GravityCompat.START)
            R.id.menu_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onFragment(areaType: String, areaName:String) {
        main_toolBar_title.text = areaName
        SunnyWeatherApplication.areaType.value = areaType
        SunnyWeatherApplication.areaName.value = if (areaName == "全部推荐") "all" else areaName
        areaPopup.dismiss()
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

    /**
     * 动态创建shortcuts
     * 设置,搜索
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private fun createDynamicShortcut(themeActived:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            if (mShortcutManager == null) {
                mShortcutManager = getSystemService(ShortcutManager::class.java)
            }

            //设置
            val settingIntent = Intent(this, SettingActivity::class.java)
            settingIntent.action = "android.intent.action.VIEW"
            val settingIcon:Icon = if (themeActived != R.style.nightTheme) {
                Icon.createWithResource(this, R.drawable.shortcut_settings_24)
            }else{
                Icon.createWithResource(this, R.drawable.shortcut_settings_night_24)
            }
            val settingShortcut = ShortcutInfo.Builder(this, "setting")
                .setIcon(settingIcon)
                .setShortLabel(getString(R.string.shortcuts_setting))
                .setLongLabel(getString(R.string.shortcuts_setting))
                .setIntent(settingIntent)
                .build()

            //搜索
            val searchIntent = Intent(this, SearchActivity::class.java)
            searchIntent.action = "android.intent.action.VIEW"
            val searchIcon:Icon = if (themeActived != R.style.nightTheme) {
                Icon.createWithResource(this, R.drawable.shortcut_search)
            }else{
                Icon.createWithResource(this, R.drawable.shortcut_search_night)
            }
            val searchShortcut = ShortcutInfo.Builder(this, "search")
                .setIcon(searchIcon)
                .setShortLabel(getString(R.string.shortcuts_search))
                .setLongLabel(getString(R.string.shortcuts_search))
                .setIntent(searchIntent)
                .build()
            mShortcutManager!!.dynamicShortcuts = arrayOf(settingShortcut,searchShortcut).toMutableList()
        }
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
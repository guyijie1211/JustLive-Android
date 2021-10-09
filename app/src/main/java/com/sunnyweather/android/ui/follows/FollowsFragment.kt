package com.sunnyweather.android.ui.follows

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_follows.*

private const val NUM_PAGES = 2
private var showLogin = false

class FollowsFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //ViewPager2
        viewPager = viewpage_follows
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        //tabLayout
        TabLayoutMediator(tab_follows, viewpage_follows){tab, position ->
            when(position) {
                0 -> tab.text = "直播中"
                else -> tab.text = "未开播"
            }
        }.attach()
    }

//    override fun onResume() {
//        super.onResume()
//        if (!SunnyWeatherApplication.isLogin.value!! && !showLogin) {
//            showLogin = true
//            MaterialAlertDialogBuilder(requireContext())
//                .setTitle("启用关注")
//                .setMessage("登录后获取关注列表")
//                .setCancelable(false)
//                .setNegativeButton("返回") { _, _ ->
//                    showLogin = false
//                    val main: MainActivity = context as MainActivity
//                    main.toFirst()
//                }
//                .setPositiveButton("登录") { _, _ ->
//                    showLogin = false
//                    val intent = Intent(context, LoginActivity::class.java)
//                    startActivity(intent)
//                }
//                .show()
//        }
//    }



    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> OnLiveFragment(true)
                1 -> OnLiveFragment(false)
                else -> Fragment()
            }
    }
}
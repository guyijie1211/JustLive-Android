package com.sunnyweather.android.util.dkplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.android.R
import kotlinx.android.synthetic.main.fragment_danmu_main.*

class DanmuMainFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_danmu_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        danmu_viewPager.adapter = PagerAdapter(this)
        TabLayoutMediator(danmu_tabLayout, danmu_viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "弹幕设置"
                }
                1 -> {
                    tab.text = "屏蔽设置"
                }
            }
        }.attach()
    }

    class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DanmuSettingFragment()
                else -> DanmuBanFragment()
            }
        }
    }
}
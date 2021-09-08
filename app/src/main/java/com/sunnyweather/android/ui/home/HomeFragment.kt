package com.sunnyweather.android.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import kotlinx.android.synthetic.main.fragment_home.*

private const val NUM_PAGES = 6

class HomeFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //ViewPager2
        viewPager = viewpage_home
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
//        viewPager.offscreenPageLimit = 5
        //tabLayout
        ViewPager2Delegate.install(viewPager, tab_home)
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> RecommendFragment("all")
                1 -> RecommendFragment("douyu")
                2 -> RecommendFragment("huya")
                3 -> RecommendFragment("bilibili")
                4 -> RecommendFragment("egame")
                5 -> RecommendFragment("cc")
                else -> Fragment()
            }
    }
}
package com.sunnyweather.android.ui.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.network.ServiceCreator
import kotlinx.android.synthetic.main.fragment_home.*

private var NUM_PAGES = 6

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
        val fragments = mutableListOf<Fragment>()
        fragments.add(RecommendFragment("all"))
        scopeNetLife {
            // 大括号内属于作用域
            val data = Get<String>(ServiceCreator.getRequestUrl() + "/api/live/getAllSupportPlatforms").await() // 发起GET请求并返回`String`
            var result: JSONArray = JSONObject.parseObject(data).getJSONObject("data").getJSONArray("platformList")
            NUM_PAGES = result.size
            for (item in result) {
                item as JSONObject
                SunnyWeatherApplication.setPlatformInfo(item.getString("code"), item.getString("name"), item.getBoolean("androidDanmuSupport"))
                val platformName = item.getString("code")
                fragments.add(RecommendFragment(platformName))
                val tabView = TextView(context)
                tabView.text = item.getString("name")
                tabView.gravity = Gravity.CENTER
                tab_home.addView(tabView)
            }
        }
        //ViewPager2
        viewPager = viewpage_home
        val pagerAdapter = ScreenSlidePagerAdapter(this, fragments)
        viewPager.adapter = pagerAdapter
//        viewPager.offscreenPageLimit = 5
        //tabLayout
        ViewPager2Delegate.install(viewPager, tab_home)
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment, private val fragments: List<Fragment>) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun getItemId(position: Int): Long {
            // 返回Fragment的唯一标识符，这里使用Fragment的hashCode作为标识符
            return fragments[position].hashCode().toLong()
        }

        override fun containsItem(itemId: Long): Boolean {
            // 检查指定的标识符是否在当前适配器中
            return fragments.any { it.hashCode().toLong() == itemId }
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

//        override fun getItemCount(): Int = NUM_PAGES
//        override fun createFragment(position: Int): Fragment =
//            when (position) {
//                0 -> RecommendFragment("all")
//                1 -> RecommendFragment("douyu")
//                2 -> RecommendFragment("huya")
//                3 -> RecommendFragment("bilibili")
//                4 -> RecommendFragment("cc")
//                else -> Fragment()
//            }
    }
}
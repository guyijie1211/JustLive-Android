package com.sunnyweather.android.ui.area

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaInfo
import kotlinx.android.synthetic.main.fragment_area.*

class AreaFragment : Fragment() {
    private var areaMap = HashMap<String, ArrayList<AreaInfo>>()
    private var areaTypeList = ArrayList<String>()
    private lateinit var viewPager: ViewPager2
    private val viewModel by lazy { ViewModelProvider(this).get(AreaViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_area, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //ViewPager2
        viewPager = viewpage_area
        if (viewModel.areaList.size < 1) {
            viewModel.areaListLiveDate?.observe(viewLifecycleOwner, { result ->
                Log.i("test","observe")
                val areas: ArrayList<ArrayList<AreaInfo>> = result.getOrNull() as ArrayList<ArrayList<AreaInfo>>
                if (areas != null) {
                    sortArea(areas)
                    val pagerAdapter = ScreenSlidePagerAdapter(this)
                    val map = areaMap
                    val list = areaTypeList
                    viewPager.adapter = pagerAdapter
                    //tabLayout
                    ViewPager2Delegate.install(viewPager, tab_area)
                } else {
                    Toast.makeText(activity, "没有更多直播间", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
        }
        viewModel.getAllAreas()
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return areaMap.size
        }
        override fun createFragment(position: Int): Fragment {
            return  AreaSingleFragment(areaMap[areaTypeList[position]]!!)
        }
    }

    private fun sortArea(areaList: List<List<AreaInfo>>){
        var areaInfoListTemp: ArrayList<AreaInfo>
        var areaType: String
        for (areaInfoList in areaList) {
            areaInfoListTemp = ArrayList()
            areaType = areaInfoList[0].typeName
            areaTypeList.add(areaType)
            areaInfoListTemp.addAll(areaInfoList)
            areaMap[areaType] = areaInfoListTemp
        }
    }
}
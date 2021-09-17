package com.sunnyweather.android.ui.area

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaInfo
import kotlinx.android.synthetic.main.fragment_area.*
import xyz.doikki.videoplayer.util.PlayerUtils

class AreaFragment : DialogFragment() {
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
                    for (areaType in areaTypeList) {
                        val textView = TextView(context)
                        textView.text = areaType
                        textView.gravity = Gravity.CENTER
                        tab_area.addView(textView)
                    }
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

    //设置打开dialog的样式
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.CustomBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_area)
        dialog.setCanceledOnTouchOutside(true)

        val window = dialog.window
        val lp = window?.attributes
        lp?.gravity = Gravity.BOTTOM
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        val point = Point()
        PlayerUtils.getWindowManager(context?.applicationContext).getDefaultDisplay().getRealSize(point)
        val with =  point.y
        lp?.height = with / 5 * 4
        lp?.windowAnimations = R.style.BottomDialogAnimation;
        window?.attributes = lp

        return dialog
    }
}
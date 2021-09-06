package com.sunnyweather.android.ui.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaInfo
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_arealist.*

class AreaSingleFragment(private val areaList: List<AreaInfo>) : Fragment() {
    private lateinit var adapter: AreaListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_arealist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = GridLayoutManager(context, 3)
        recyclerView_area.addItemDecoration(SpaceItemDecoration(10))
        recyclerView_area.layoutManager = layoutManager
        adapter = AreaListAdapter(this, areaList)
        recyclerView_area.adapter = adapter
    }
}
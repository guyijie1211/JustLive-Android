package com.sunnyweather.android.ui.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Owner
import com.sunnyweather.android.logic.provider.MySuggestionProvider
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(SearchViewModel::class.java) }
    private lateinit var searchAdapter: SearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val layoutManager = GridLayoutManager(this, 1)
        recyclerView_search.addItemDecoration(SpaceItemDecoration(10))
        recyclerView_search.layoutManager = layoutManager
        searchAdapter = SearchAdapter(this, viewModel.ownersList as List<Owner>)
        recyclerView_search.adapter = searchAdapter
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
                    .saveRecentQuery(query, null)
            }
        }
        viewModel.ownerListLiveData.observe(this, {result ->
            val rooms: ArrayList<Owner> = result.getOrNull() as ArrayList<Owner>
            if (rooms != null) {
                if (swiperefresh_search.isRefreshing) {
                    viewModel.clearList()
                }
                viewModel.ownersList.addAll(rooms)
                searchAdapter.notifyDataSetChanged()
                swiperefresh_search.isRefreshing = false
            } else {
                Toast.makeText(this, "没有更多直播间", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        intent.getStringExtra("query")?.also { query ->
            viewModel.search("all", query, "0")
        }

    }
}
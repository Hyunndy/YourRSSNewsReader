package com.example.myrealtripwithhyunndy

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RSSFeedScrollListener ( val func : () -> Unit, val layoutManager : LinearLayoutManager ) : RecyclerView.OnScrollListener() {


    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 2
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if(dy > 0) {
            visibleItemCount = recyclerView.childCount

            totalItemCount = layoutManager.itemCount

            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if(loading) {
                if(totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }

            // 로드된 아이템의 제일 마지막에 도달할 때 호출.
            if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                Log.d("TEST33", "Scroll end reached!!")

                func() // 람다식으로 넘겨받은 함수
                loading = true
            }


        }

    }


}
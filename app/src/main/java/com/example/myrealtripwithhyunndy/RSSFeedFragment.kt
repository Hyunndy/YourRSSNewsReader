package com.example.myrealtripwithhyunndy


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.myrealtripwithhyunndy.rsshelper.*
import kotlinx.android.synthetic.main.fragment_rssfeed.*
import kotlinx.android.synthetic.main.item_news.view.*


class RSSFeedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var viewModel: RSSFeedViewModel
    private lateinit var RSSFeedListAdapter: RSSFeedRecyclerViewAdapter
    private var requestJsoup = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 뷰모델
        viewModel = ViewModelProvider(requireActivity())[RSSFeedViewModel::class.java]
        viewModel.getRSSList()?.observe(viewLifecycleOwner, Observer {
                RSSFeedListAdapter.newsList = it
                RSSFeedListAdapter.notifyDataSetChanged()
                requestJsoup = false
                swipeLayout.isRefreshing = false
        })

        return inflater.inflate(R.layout.fragment_rssfeed, container, false)
    }

    interface OnNewsSelectedListner {
        fun onNewsSelected(selectedIdx: Int)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 어댑터 연결
        RSSFeedListAdapter = RSSFeedRecyclerViewAdapter { (context as OnNewsSelectedListner).onNewsSelected(it) }

        setRecyclerAdapter()

        rss_recylerview.adapter = RSSFeedListAdapter

        //swipe리스너
        swipeLayout.setOnRefreshListener(this)
    }

    fun setRecyclerAdapter() {
        rss_recylerview.apply {
            setHasFixedSize(true)
            val linearLayout = LinearLayoutManager(context)
            layoutManager = linearLayout
            clearOnScrollListeners()
            addOnScrollListener(RSSFeedScrollListener({ viewModel.getDetailNews() }, linearLayout))
        }
    }

    override fun onRefresh() {
        RSSFeedListAdapter.newsList?.clear()
        RSSFeedListAdapter.notifyDataSetChanged()
        setRecyclerAdapter()
        viewModel.refresh()
        viewModel.loadRSSList()
    }

    //리사이클러뷰 어댑터
    inner class RSSFeedRecyclerViewAdapter(val clickedNews: (Int) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var newsList: MutableList<RSSItem>? = null

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)



        @SuppressLint("InflateParams")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CustomViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = holder.itemView

            // 뉴스 타이틀
            view.news_title.text = newsList?.get(position)?.title

            // 뉴스 설명
            view.news_desc.text = newsList?.get(position)?.description

            // 이미지
            Glide.with(view.news_thumbnail).asBitmap().load(newsList?.get(position)?.imgLink)
                .error(R.drawable.ic_newspaper).into((view.news_thumbnail))

            // 클릭 이벤트
            view.setOnClickListener {
                clickedNews(position)
            }
        }

        override fun getItemCount(): Int {
            return newsList?.size ?: 0
        }

    }

    inner class RSSFeedScrollListener(
        val func: () -> Unit,
        val layoutManager: LinearLayoutManager
    ) : RecyclerView.OnScrollListener() {

        private var previousTotal = 0
        private var loading = true
        private var firstVisibleItem = 0
        private var visibleItemCount = 0
        private var totalItemCount = 0


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0) {
                visibleItemCount = recyclerView.childCount
                totalItemCount = layoutManager.itemCount
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (loading) {
                     if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }

                // 로드된 아이템의 제일 마지막에 도달할 때 호출.
                if (!requestJsoup && !loading && (visibleItemCount + firstVisibleItem) >= totalItemCount && firstVisibleItem >= 0 && totalItemCount >= 6) {

                    requestJsoup = true
                    func() // 람다식으로 넘겨받은 함수
                    loading = true

                }
            }

        }
    }


}



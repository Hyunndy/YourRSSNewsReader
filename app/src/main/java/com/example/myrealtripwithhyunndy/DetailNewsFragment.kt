package com.example.myrealtripwithhyunndy


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.example.myrealtripwithhyunndy.rsshelper.RSSFeedViewModel
import com.example.myrealtripwithhyunndy.rsshelper.RSSItem
import kotlinx.android.synthetic.main.fragment_detailnews.*

class DetailNewsFragment : Fragment() {

    private lateinit var viewModel : RSSFeedViewModel
    private var newsItem : RSSItem? = null
    private var selectedIdx : Int = 0

    companion object {
        fun getInstance(index : Int) : Fragment {

            val fragment = DetailNewsFragment()
            fragment.selectedIdx = index
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_detailnews, container, false)


    @SuppressLint("ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RSSFeedViewModel::class.java]
        newsItem = getNews(viewModel)

        if(newsItem?.link != "") {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }

            val webSettings = news_web.settings

            news_web.webViewClient = WebViewClient()
            webSettings.javaScriptEnabled = true
            webSettings.javaScriptCanOpenWindowsAutomatically = false

            news_web.loadUrl(newsItem?.link)
        }

        news_title_detail.text = newsItem?.title

    }

    var getNews : (RSSFeedViewModel) -> RSSItem? = { model : RSSFeedViewModel ->
        val movieList = model.getRSSList()?.value
        movieList?.get(selectedIdx) }
}


package com.example.myrealtripwithhyunndy

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myrealtripwithhyunndy.news.NewsDTO
import com.example.myrealtripwithhyunndy.news.NewsListRecyclerViewAdapter
import com.example.myrealtripwithhyunndy.rsshelper.RSSHelper

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URL


enum class NEWSLISTUPDATE(var value : Int) {
    UPDATE(10)
}

enum class RESULTCODE(var value : Int) {
    SHOWDETAIL(100)
}

enum class ACTIVITYSTATE(var value : Int) {
    IDLE(100),
    UPDATE(200)
}

class MainActivity : AppCompatActivity() {

    private val rssURL = URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko")

    private val newsItemListAdapter = NewsListRecyclerViewAdapter { openDetailNewsPage(it) }

    var activityState = ACTIVITYSTATE.IDLE.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newsList.adapter = newsItemListAdapter
        newsList.layoutManager = LinearLayoutManager(this)

        RSSHelper(this).execute(rssURL)

        swipeLayout.setOnRefreshListener {
            RSSHelper(this).execute(rssURL)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDetailNewsPage(selectedNews : NewsDTO) {

        if(activityState == ACTIVITYSTATE.UPDATE.value) { return }

        var intent = Intent(this, DetailNewsActivity::class.java)
        intent.putExtra("news", selectedNews)
        setResult(RESULTCODE.SHOWDETAIL.value, intent)
        Toast.makeText(applicationContext, "상세 페이지를 엽니다.", Toast.LENGTH_LONG).show()
        startActivity(intent)
    }

    fun updateRSSNewsList(rssNewsTitleList : ArrayList<String>, rssNewsLinkList : ArrayList<String>, rssNewsThumbnailList : ArrayList<String>, rssNewsDescList : ArrayList<String> , rssNewsNum : Int , rssKeywordList : ArrayList<ArrayList<String>>) {

        // 어댑터에 들어갈 최종 리스트
        var rssNewsList = ArrayList<NewsDTO>()

        for(idx in 1 until rssNewsNum) {

          var newsItem = NewsDTO()

          newsItem.link = rssNewsLinkList[idx]
          newsItem.title = rssNewsTitleList[idx]
          newsItem.thumbnail = rssNewsThumbnailList[idx]

          newsItem.desc = rssNewsDescList[idx]

            var tempArray = rssKeywordList[idx]
           newsItem.keyword1 = tempArray[0]
           newsItem.keyword2 = tempArray[1]
           newsItem.keyword3 = tempArray[2]

            rssNewsList.add(newsItem)
        }

        updateNewsList(rssNewsList)
    }

    private fun updateNewsList(updatedNewsList : ArrayList<NewsDTO>) {
        newsItemListAdapter.updateNewsList(updatedNewsList, NEWSLISTUPDATE.UPDATE.value)
    }
}

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

/*
마이리얼트립 과제
~RSS 뉴스 리더 어플리케이션~

기능 2. 뉴스 리스트

2-1) 아이템 구성
1. 썸네일, 제목, 본문의 일부, 주요 키워드 3개
2. 각 뉴스 항목(item)을 선택하면 뉴스 상세보기 화면으로 넘어갈 수 있게
3. 당기면 리스트 업데이트

2-2) RSS 에서 가져오기
1. RSS 란? (Really Simple Syndication)
- 정말 간단한 배급
- 웹사이트 or 블로그에서 제공하는 RSS 주소를 리더에 등록하면 직접 방문하지 않아도 자동으로 자료가 업데이트 되어 쉽게 새로운 컨텐츠를 확인할 수 있는 인터넷 기술.
- 각 사이트에서 해당 정보를 제공해줘야만(조건) 얻을 수 있는 데이터로써, XML 의 데이터 타입으로 얻어진다.
 */

enum class NEWSLISTUPDATE(var value : Int) {
    UPDATE(10)
}

enum class RESULTCODE(var value : Int) {
    SHOWDETAIL(100)
}

class MainActivity : AppCompatActivity() {

    private val newsItemListAdapter = NewsListRecyclerViewAdapter { it -> openDetailNewsPage(it) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        newsList.adapter = newsItemListAdapter
        newsList.layoutManager = LinearLayoutManager(this)

        var rssURL = URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko")


        // TODO : AsyncTask 에서 처리하던 ImageLoad 등을 MainThread 에서 하게 변경
        var rssNewsReaderTask = RSSHelper(this)
        rssNewsReaderTask.execute(rssURL)

        swipeLayout.setOnRefreshListener {
            // RSS를 다시 받아와야함.
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 뉴스를 클릭하면 뉴스 상세 페이지로 이동하게
    private fun openDetailNewsPage(selectedNews : NewsDTO) {

        var intent = Intent(this, DetailNewsActivity::class.java)
        intent.putExtra("news", selectedNews)
        setResult(RESULTCODE.SHOWDETAIL.value, intent)
        Toast.makeText(applicationContext, "상세 페이지를 엽니다.", Toast.LENGTH_LONG).show()
        startActivity(intent)
    }

    private fun updateNewsList(updatedNewsList : ArrayList<NewsDTO>) {

        Toast.makeText(applicationContext, "뉴스리스트를 업데이트 합니다.", Toast.LENGTH_LONG).show()

        newsItemListAdapter.updateNewsList(updatedNewsList, NEWSLISTUPDATE.UPDATE.value)
    }

    // 1. 타이틀
    // 2. 링크
    // 3. 아이템 수
    fun updateRSSNewsList(rssNewsTitleList : ArrayList<String>, rssNewsLinkList : ArrayList<String>, rssNewsThumbnailList : ArrayList<String>, rssNewsDescList : ArrayList<String> , rssNewsNum : Int) {

        // 어댑터에 들어갈 최종 리스트
        var rssNewsList = ArrayList<NewsDTO>()

        for(idx in 1..rssNewsNum) {

            var newsItem = NewsDTO()

            newsItem.link = rssNewsLinkList[idx]
            newsItem.title = rssNewsTitleList[idx]
            newsItem.thumbnail = rssNewsThumbnailList[idx]
            newsItem.desc = rssNewsDescList[idx]

            rssNewsList.add(newsItem)
        }

        updateNewsList(rssNewsList)
    }
}

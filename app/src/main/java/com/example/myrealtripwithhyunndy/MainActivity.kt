package com.example.myrealtripwithhyunndy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myrealtripwithhyunndy.news.NewsDTO
import com.example.myrealtripwithhyunndy.news.NewsListRecyclerViewAdapter
import com.example.myrealtripwithhyunndy.rsshelper.RSSFeedViewModel
import com.example.myrealtripwithhyunndy.rsshelper.RssListManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URL


/*
created by hyeonjiy 20.04.03

뉴스 리스트가 표시되는 액티비티.

1. 생성 시 RSSHelper AsyncTask가 실행되며 RSS 목록을 로드함.
2. RSSHelper AsyncTask가 끝나는 시점에서 RecyclerView 업데이트. ( 어댑터: NewsListRecyclerViewAdapter )
3. 뉴스 선택 시 DetailNewsActivity 실행.
 */

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

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private lateinit var model : RSSFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // rss 목록 로드를 위한 뷰모델
        model = ViewModelProvider(this)[RSSFeedViewModel::class.java]

        // rss 목록 프래그먼트 실행
        supportFragmentManager.beginTransaction().replace(R.id.mainframe, RSSFeedFragment()).commit()


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




}

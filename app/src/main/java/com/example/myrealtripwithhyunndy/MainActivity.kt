package com.example.myrealtripwithhyunndy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrealtripwithhyunndy.news.NewsDTO
import com.example.myrealtripwithhyunndy.news.NewsListRecyclerViewAdapter

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/*
마이리얼트립 과제
~RSS 뉴스 리더 어플리케이션~

기능 2. 뉴스 리스트

2-1) 아이템 구성
1. 썸네일, 제목, 본문의 일부, 주요 키워드 3개
 */

enum class NEWSLISTUPDATE(var value :Int) {
    UPDATE(1)
}

class MainActivity : AppCompatActivity() {

    var newsItemListAdapter = NewsListRecyclerViewAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        newsList.adapter = newsItemListAdapter
        newsList.layoutManager = LinearLayoutManager(this)

        //getNewsList()

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

    fun getNewsList() {

        var newsList:ArrayList<NewsDTO>? = intent.getParcelableArrayListExtra("newsList")

        newsItemListAdapter.updateNewsList(newsList, NEWSLISTUPDATE.UPDATE.value)

    }
}

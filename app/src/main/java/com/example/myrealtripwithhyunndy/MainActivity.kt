package com.example.myrealtripwithhyunndy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.myrealtripwithhyunndy.rsshelper.RSSFeedViewModel


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
class MainActivity : AppCompatActivity(), RSSFeedFragment.OnNewsSelectedListner {

    private lateinit var model : RSSFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // rss 목록 로드를 위한 뷰모델
        model = ViewModelProvider(this)[RSSFeedViewModel::class.java]
        model.loadRSSList()

        // rss 목록 프래그먼트 실행
        supportFragmentManager.beginTransaction().replace(R.id.mainframe, RSSFeedFragment()).commit()
    }

    // RSS 목록의 아이템이 눌렸을 경우
    override fun onNewsSelected(selectedIdx: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(
            R.id.mainframe,
            DetailNewsFragment.getInstance(selectedIdx)
        )
        ft.commit()

    }
}

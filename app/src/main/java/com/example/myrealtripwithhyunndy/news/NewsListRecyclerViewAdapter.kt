package com.example.myrealtripwithhyunndy.news

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrealtripwithhyunndy.NEWSLISTUPDATE
import com.example.myrealtripwithhyunndy.R
import kotlinx.android.synthetic.main.news_item.view.*
import java.net.URI

/*
MainActivity 의 NewList RecyclerView 를 위한 뷰홀더.
 */

class NewsListRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    var newsItemList : ArrayList<NewsDTO> = arrayListOf()

    init {
        var initNews = NewsDTO()

        initNews.thumbnail = "https://t1.daumcdn.net/cfile/tistory/2477D2385656BD722D"
        initNews.title = "무지야 사랑해"
        initNews.desc = "무지 너무 귀여운것으로 밝혀져... 삼성에서는 무지 체크카드를 발급해줘야한다."
        initNews.keyword1 = "카카오"
        initNews.keyword2 = "튜브"
        initNews.keyword3 = "콘"

        newsItemList.add(initNews)
    }

    fun updateNewsList(newNewsList : ArrayList<NewsDTO>?, updateCode : Int) {

        when(updateCode) {
           //NEWSLISTUPDATE.ADD.value -> {
           //    commentList.add(newCommentList!![0])
           //}
            NEWSLISTUPDATE.UPDATE.value -> {
                newsItemList.clear()
                newsItemList = newNewsList!!
            }
        }
        notifyDataSetChanged()
    }

    inner class NewsListViewHolder(view : View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // 아이템 레이아웃 inflate
        var newsItemLayout = LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent, false)

        return NewsListViewHolder(newsItemLayout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var newsItem = (holder as NewsListViewHolder).itemView

        var thumbUri :Uri? = Uri.parse(newsItemList[position].thumbnail)
        Log.d("test1", "${thumbUri}")
        Glide.with(newsItem.news_thumbnail).asBitmap().load(thumbUri).into(newsItem.news_thumbnail)
        newsItem.news_title.text = newsItemList[position].title
        newsItem.news_desc.text = newsItemList[position].desc
        newsItem.news_keyword1.text = (newsItemList[position].keyword1)
        newsItem.news_keyword2.text = (newsItemList[position].keyword2)
        newsItem.news_keyword3.text = (newsItemList[position].keyword3)
    }

    override fun getItemCount(): Int {
        return newsItemList.size
    }

}
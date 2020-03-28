package com.example.myrealtripwithhyunndy.news

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrealtripwithhyunndy.NEWSLISTUPDATE
import com.example.myrealtripwithhyunndy.R
import kotlinx.android.synthetic.main.news_item.view.*
import org.w3c.dom.Text
import java.net.URI

/*
MainActivity 의 NewList RecyclerView 를 위한 뷰홀더.
 */

class NewsListRecyclerViewAdapter(val itemClick : (NewsDTO) -> Unit) : RecyclerView.Adapter<NewsListRecyclerViewAdapter.NewsListViewHolder> () {

    var newsItemList : ArrayList<NewsDTO> = arrayListOf()

  // init {
  //     var initNews = NewsDTO()

  //     initNews.thumbnail = "https://t1.daumcdn.net/cfile/tistory/2477D2385656BD722D"
  //     initNews.title = "무지야 사랑해"
  //     initNews.desc = "무지 너무 귀여운것으로 밝혀져... 삼성에서는 무지 체크카드를 발급해줘야한다."
  //     initNews.keyword1 = "카카오"
  //     initNews.keyword2 = "튜브"
  //     initNews.keyword3 = "콘"

  //     newsItemList.add(initNews)

  //     initNews.title = "무지야 오랑해"
  //     newsItemList.add(initNews)

  // }

    fun updateNewsList(newNewsList : ArrayList<NewsDTO>?, updateCode : Int) {

        when(updateCode) {
            NEWSLISTUPDATE.UPDATE.value -> {
                newsItemList.clear()
                newsItemList = newNewsList!!
            }
        }
        notifyDataSetChanged()
    }

    inner class NewsListViewHolder(itemView: View, itemClick: (NewsDTO) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val newsThumbnail = itemView?.findViewById<ImageView>(R.id.news_thumbnail)
        val newsTitle = itemView?.findViewById<TextView>(R.id.news_title)
        val newsDesc = itemView?.findViewById<TextView>(R.id.news_desc)
        val newsKeyword1 = itemView?.findViewById<TextView>(R.id.news_keyword1)
        val newsKeyword2 = itemView?.findViewById<TextView>(R.id.news_keyword2)
        val newsKeyword3 = itemView?.findViewById<TextView>(R.id.news_keyword3)

        fun bind(newsItem : NewsDTO) {
            if(newsItem.thumbnail != "noImage") {
                var thumbUri :Uri? = Uri.parse(newsItem.thumbnail)
                Glide.with(newsThumbnail).asBitmap().load(thumbUri).error(R.drawable.ic_newspaper).into(newsThumbnail)
            } else {
                newsThumbnail.setImageResource(R.drawable.ic_newspaper)
            }

            newsTitle.text = newsItem.title
            newsDesc.text = newsItem.desc
            newsKeyword1.text = newsItem.keyword1
            newsKeyword2.text = newsItem.keyword2
            newsKeyword3.text = newsItem.keyword3

            // 1. Adapter의 파라미터에 (val itemClick : (NewsDTO) -> Unit) -> NewsItem을 파라미터로 받아서, 아무것도 반환하지않는 파라미터를 람다식으로 넣어놓음.
            // 2. 어댑터 내에서 setOnClickListener 기능을 설정 할 때 (NewDTO -> Unit) 에 해당하는 함수 자체를 하나의 변수로 꺼내 쓸 수 있다.
            itemView.setOnClickListener {
                itemClick(newsItem)
                // 3. itemView가 클릭됐을 때 처리할 일을 itemClick으로 설정한다. (NewsDTO) -> Unit 에 대한 함수는 나중에 MainActivity.kt 클래스에서 작성한다. */
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {

        // 아이템 레이아웃 inflate
        var newsItemLayout = LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent, false)

        return NewsListViewHolder(newsItemLayout, itemClick)
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(newsItemList[position])
    }

    override fun getItemCount(): Int {
        return newsItemList.size
    }

}
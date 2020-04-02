package com.example.myrealtripwithhyunndy.news

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrealtripwithhyunndy.NEWSLISTUPDATE
import com.example.myrealtripwithhyunndy.R

/*
MainActivity 의 NewList RecyclerView 를 위한 뷰홀더.
 */

class NewsListRecyclerViewAdapter(val itemClick : (NewsDTO) -> Unit) : RecyclerView.Adapter<NewsListRecyclerViewAdapter.NewsListViewHolder> () {

    var newsItemList : ArrayList<NewsDTO> = arrayListOf()

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

        private val newsThumbnail = itemView.findViewById<ImageView>(R.id.news_thumbnail)
        private val newsTitle = itemView.findViewById<TextView>(R.id.news_title)
        private val newsDesc = itemView.findViewById<TextView>(R.id.news_desc)
        private val newsKeyword1 = itemView.findViewById<TextView>(R.id.news_keyword1)
        private val newsKeyword2 = itemView.findViewById<TextView>(R.id.news_keyword2)
        private val newsKeyword3 = itemView.findViewById<TextView>(R.id.news_keyword3)

        fun bind(newsItem : NewsDTO) {
            if(newsItem.thumbnail != "noImage") {
                var thumbUri :Uri? = Uri.parse(newsItem.thumbnail)
                Glide.with(newsThumbnail).asBitmap().load(thumbUri).error(R.drawable.ic_newspaper).into(newsThumbnail)
            } else {
                newsThumbnail.setImageResource(R.drawable.ic_newspaper)
            }

            newsTitle.text = newsItem.title
            newsDesc.text = newsItem.desc

            if(newsItem.keyword1 == "") {
                newsKeyword1.visibility = View.INVISIBLE
                newsKeyword2.visibility = View.INVISIBLE
                newsKeyword3.visibility = View.INVISIBLE
            } else {
                newsKeyword1.visibility = View.VISIBLE
                newsKeyword2.visibility = View.VISIBLE
                newsKeyword3.visibility = View.VISIBLE

                newsKeyword1.text = newsItem.keyword1
                newsKeyword2.text = newsItem.keyword2
                newsKeyword3.text = newsItem.keyword3
            }


            // 1. Adapter의 파라미터에 (val itemClick : (NewsDTO) -> Unit) -> NewsItem을 파라미터로 받아서, 아무것도 반환하지않는 파라미터를 람다식으로 넣어놓음.
            // 2. 어댑터 내에서 setOnClickListener 기능을 설정 할 때 (NewDTO -> Unit) 에 해당하는 함수 자체를 하나의 변수로 꺼내 쓸 수 있다.
            // 3. itemView가 클릭됐을 때 처리할 일을 itemClick으로 설정한다. (NewsDTO) -> Unit 에 대한 함수는 나중에 MainActivity.kt 클래스에서 작성한다.
            itemView.setOnClickListener {
                itemClick(newsItem)

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
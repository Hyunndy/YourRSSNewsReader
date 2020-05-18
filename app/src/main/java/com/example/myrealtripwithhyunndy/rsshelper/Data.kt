package com.example.myrealtripwithhyunndy.rsshelper

import android.os.Parcelable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/*

API에서 받아오는건 APIModels.

여긴 API에서 받아온걸 실제로 쓰는 데이터 클래스.


 */

 data class RSSChannel(
     var page : Int = 0,
     var items : List<RSSItem>? = null
 )

 data class RSSItem(
    var title : String? = "",
    var link : String? = "",
    var description : String? = "",
    var imgLink : String? = "",
    var keyword : ArrayList<String>? = null
)

interface ItemAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}

interface ViewType {
    fun getViewType(): Int
}

object AdapterType {
    val NEWS = 1
    val LOADING = 2
}

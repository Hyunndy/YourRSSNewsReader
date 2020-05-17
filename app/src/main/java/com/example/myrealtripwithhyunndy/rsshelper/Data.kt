package com.example.myrealtripwithhyunndy.rsshelper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/*

API에서 받아오는건 APIModels.

여긴 API에서 받아온걸 실제로 쓰는 데이터 클래스.


 */

 data class RSSList(
     var page : Int = 0,
     var items : MutableList<RSSItem>? = null
 )

 data class RSSItem(
    var title : String? = "",
    var link : String? = "",
    var description : String? = "",
    var imgLink : String? = "",
    var keyword : ArrayList<String>? = null
)



package com.example.myrealtripwithhyunndy.news

import android.os.Parcel
import android.os.Parcelable

/*
뉴스리스트 전달용 DTO.

1. 뉴스 썸네일
2. 뉴스 제목
3. 뉴스 내용
4. 키워드 3개
 */

class NewsDTO  : Parcelable {

    var thumbnail : String ? = null
    var title : String ? = null
    var desc : String ? = null
    var keyword1 : String ? = null
    var keyword2 : String ? = null
    var keyword3 : String ? = null

    companion object {
        @JvmField

        val CREATOR:Parcelable.Creator<NewsDTO> = object : Parcelable.Creator<NewsDTO> {

            override fun createFromParcel(source: Parcel?): NewsDTO {

                var news = NewsDTO()

                news.thumbnail = source?.readString()
                news.title = source?.readString()
                news.desc = source?.readString()
                news.keyword1 = source?.readString()
                news.keyword2 = source?.readString()
                news.keyword3 = source?.readString()

                return news
            }

            override fun newArray(size: Int): Array<NewsDTO?> {
                return arrayOfNulls<NewsDTO>(size)
            }
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(thumbnail)
        dest?.writeString(title)
        dest?.writeString(desc)
        dest?.writeString(keyword1)
        dest?.writeString(keyword2)
        dest?.writeString(keyword3)
    }

    override fun describeContents(): Int {
       return 0
    }
}
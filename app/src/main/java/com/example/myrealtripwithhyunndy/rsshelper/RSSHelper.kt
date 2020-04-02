package com.example.myrealtripwithhyunndy.rsshelper

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils.indexOf
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.myrealtripwithhyunndy.ACTIVITYSTATE
import com.example.myrealtripwithhyunndy.MainActivity
import com.example.myrealtripwithhyunndy.news.NewsDTO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.Integer.min
import java.net.ContentHandler
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import kotlin.math.min
import java.util.regex.Pattern as Pattern1

enum class STATE(var value : Int) {
    IDLE(0),
    TITLE(10),
    LINK(20),
}

// 1. excute()에 rss피드 주소를 넣어야 하므로 String
class RSSHelper(var context: Context) : AsyncTask<URL, String, String> () {


    var rssNewsNum = 0
    var rssNewsTitle = arrayListOf<String>()
    var rssNewsLink = arrayListOf<String>()
    var rssNewsThumbnail = arrayListOf<String>()
    var rssNewsDesc = arrayListOf<String>()

    var keywordHelper = KeywordExtractionHelper()


    override fun onPreExecute() {
        super.onPreExecute()

        var main = context as MainActivity
        main.swipeLayout.isRefreshing = true
        main.swipeLayout.isEnabled = false
        main.progressBar.visibility = View.VISIBLE
        main.activityState = ACTIVITYSTATE.UPDATE.value

        Toast.makeText(context,"뉴스 리스트를 업로드 중 입니다.", Toast.LENGTH_LONG).show()
    }

    inner class RSSHandler : DefaultHandler() {

        var newsTitle = ""
        var newsLink = ""

        var state = STATE.IDLE.value

        override fun startDocument() {
            super.startDocument()

            rssNewsNum = 0
        }


        // 요소가 시작될 때
        override fun startElement(
            uri: String?,
            localName: String?,
            qName: String?,
            attributes: Attributes?
        ) {
            if(qName.equals("item")) {
                rssNewsNum++
            }

            if(qName.equals("title"))  {

                newsTitle = ""
                state = STATE.TITLE.value // state 세팅
            }
            if(qName.equals("link")) {

                newsLink = ""
                state = STATE.LINK.value
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {


           if(qName.equals("title")) {
               rssNewsTitle.add(newsTitle)
           }

            if(qName.equals("link")) {
                rssNewsLink.add(newsLink)
            }

            state = STATE.IDLE.value
        }

        // 요소의 값을 읽어오는 함수인데 현재 읽어온 값의 상태값이 타이틀인지 판단해서 맞다면 List에 저장한다.
        override fun characters(ch: CharArray?, start: Int, length: Int) {

            if(state == STATE.TITLE.value) {
                var tempString  = String(ch!!, start, length)
                newsTitle += tempString
            }

            if(state == STATE.LINK.value) {
                var tempString  = String(ch!!, start, length)
                if(tempString == "") {
                    newsLink += "FailToLoadURL"
                } else {
                    newsLink += tempString
                }
            }
        }
    }

    //1.  RSS 피드를 받아와서 XML 값을 가져와서 파싱하는 코드가 들어옵니다.
    override fun doInBackground(vararg params: URL?): String {

        try {

            //어플리케이션이 SAX 베이스의 파서를 구성 및 취득해 XML 문서를 구문 분석 할 수 있도록 하는 팩토리 API를 정의합니다.
            var mSAXParserFactory = SAXParserFactory.newInstance()

            // SAX 파서
            var mSAXParser = mSAXParserFactory.newSAXParser()

            // SAX 파서를 위한 XML 리더
            var mXMLReader = mSAXParser.xmlReader

            // XML 리더를 위한 핸들러
            var mRSSHandler = RSSHandler()
            mXMLReader.contentHandler = mRSSHandler

            var mHttpConnection : HttpURLConnection = params[0]?.openConnection() as HttpURLConnection
            var rsCode = mHttpConnection.responseCode
            if(rsCode == HttpURLConnection.HTTP_OK) {

                var mStreamReader = InputStreamReader(mHttpConnection.inputStream, "UTF-8")
                var mInputResource = InputSource(mStreamReader)
                mXMLReader.parse(mInputResource)
            }

            //// // 링크로부터 본문&이미지 로딩
            for(idx in rssNewsLink.indices) {
                extractImageandDescFromLink(rssNewsLink[idx])
            }
        } catch (e : Exception) {
            Toast.makeText(context,"RSS 읽어오기를 실패했습니다.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }


        return ""
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: String?) {

       val keywordList = keywordHelper.extractKeywordsFromJSON()

        // 여기서 뉴스 리스트를 업데이트 시킨다.
        var main = context as MainActivity
        main.updateRSSNewsList(rssNewsTitle, rssNewsLink,  rssNewsThumbnail, rssNewsDesc, rssNewsNum, keywordList)

        main.swipeLayout.isRefreshing = false
        main.swipeLayout.isEnabled = true
        main.progressBar.visibility = View.GONE
        main.activityState = ACTIVITYSTATE.IDLE.value

        super.onPostExecute(result)
    }

    // 링크로부터 이미지 URL 링크 받아옴 -> 어댑터에서 String 값으로 Glide 로 이미지 출력.
    private fun extractImageandDescFromLink(link : String){

        var newsDesc = ""
        var imgLink = ""


        if(link == "FailToLoadURL") {
            imgLink = "noImage"
            newsDesc = "......"
        } else {
                try {
                   var doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get()
                   imgLink = doc.select("meta[property=og:image]")[0].attr("content")
                   newsDesc = doc.select("meta[property=og:description]")[0].attr("content")

                } catch ( e : Exception) {
                    imgLink = "noImage"
                    newsDesc = "......"

                    e.printStackTrace()
                }
        }

        rssNewsThumbnail.add(imgLink)
        rssNewsDesc.add(newsDesc)

        keywordHelper.getJSONFromDesc(newsDesc)
    }
}
package com.example.myrealtripwithhyunndy.rsshelper

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import com.example.myrealtripwithhyunndy.ACTIVITYSTATE
import com.example.myrealtripwithhyunndy.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.SAXParserFactory

enum class STATE(var value : Int) {
    IDLE(0),
    TITLE(10),
    LINK(20),
}

/*
created by hyeonjiy 20.04.03

RSS 목록을 가져오기 위한 클래스.

1. SAX Parser를 이용해 구글 RSS 피드의 XML 파일을 파싱한다.
2. 1의 과정에서 title, link, 뉴스 갯수를 추출한다.
3. 2에서 얻은 link 에서 Jsoup 라이브러리를 이용해 썸네일, 본문을 읽어온다.
4. 3에서 얻은 본문에서 KeywordExtractionHelper 클래스에서 https://www.adams.ai/apiPage?keywordextract 키워드 추출 api를 이용해서 키워드 3개를 추출한다.
5. 위 과정들이 완료되면 MainActivity의 RecyclerView에 올라갈 리스트를 업데이트 해준다.
 */

/*
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

    // RSS에서 얻어온 XML을 파싱할 SAX 핸들러
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

    override fun doInBackground(vararg params: URL?): String {

        try {
            val mSAXParserFactory = SAXParserFactory.newInstance()

            val mSAXParser = mSAXParserFactory.newSAXParser()

            val mXMLReader = mSAXParser.xmlReader

            val mRSSHandler = RSSHandler()
            mXMLReader.contentHandler = mRSSHandler

            val mHttpConnection : HttpURLConnection = params[0]?.openConnection() as HttpURLConnection
            val rsCode = mHttpConnection.responseCode
            if(rsCode == HttpURLConnection.HTTP_OK) {

                val mStreamReader = InputStreamReader(mHttpConnection.inputStream, "UTF-8")
                val mInputResource = InputSource(mStreamReader)
                mXMLReader.parse(mInputResource)
            }

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

        var main = context as MainActivity
        main.updateRSSNewsList(rssNewsTitle, rssNewsLink,  rssNewsThumbnail, rssNewsDesc, rssNewsNum, keywordList)

        main.swipeLayout.isRefreshing = false
        main.swipeLayout.isEnabled = true
        main.progressBar.visibility = View.GONE
        main.activityState = ACTIVITYSTATE.IDLE.value

        super.onPostExecute(result)
    }

    // Jsoup 라이브러리를 이용해 이미지, 본문을 추출한다.
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

        // 본문을 통해서 키워드 추출 api를 통해 얻은 JSON파일을 추출한다.
        keywordHelper.getJSONFromDesc(newsDesc)
    }
}

 */
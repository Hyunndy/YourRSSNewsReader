package com.example.myrealtripwithhyunndy.rsshelper

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils.indexOf
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.net.toUri
import com.example.myrealtripwithhyunndy.MainActivity
import com.example.myrealtripwithhyunndy.news.NewsDTO
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.lang.Exception
import java.lang.Integer.min
import java.net.ContentHandler
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import kotlin.math.min
import java.util.regex.Pattern as Pattern1

/*
RSS 처리를 위한 AsyncTask 클래스.

1. 스레드안에서 실행될 코드는 doInBackground() 에 넣어두고 UI에 접근할 코드는 나머지에 넣어둔다.

2. AsyncTask 도 스레드를 실행하는 것과 같기 때문에 스레드 안에서 실행될 대부분의 코드는 doInBackground() 안에 들어가있게 되며 중간중간 화면에 표시하기 위한 코드 실행을 위해 onProgressUpdate()가 호출되는 것이다.

3. onProgressUpdate()는 doInBackground()안에서 publishProgress()가 호출될 때마다 자동으로 호출된다.
 */

/*
1. inner class AsyncTaskClass : AsyncTask<Int, Long, String>()

제네릭 타입을 3개 지정해주어야 한다.
제네릭1) excute() 매개변수 타입
제네릭2) publishProgress() 매개변수 타입
제네릭3) doInBackground()의 반환 타입 이자 onPostExcute의 매개변수 타입
 */


/*
SAX PARSER 란?
<Simple API for XML>
이벤트 중심의 인터페이스다.
프로그래머가 일어날 수 있는 이벤트를 설정해 놓으면, SAX는 그 이벤트가 일어났을 때 제어권을 갖고 상황을 처리한다.
 */


data class extractedKeyword (
    var term : String = "",
    var weight : Double = .0
)

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

        (context as MainActivity).swipeLayout.isRefreshing = true
        (context as MainActivity).swipeLayout.isEnabled = false
        Toast.makeText(context,"RSS를 읽어옵니다.", Toast.LENGTH_LONG).show()
    }

    // XML 문서가 시작되고 끝날 때, 요소가 시작될 때와 종료될 때 호출되는 함수.
    // 이곳에서 값을 읽어와 문자열을 구성하는 것이다.
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

            // URL을 읽어서 파일을 가져옴
            var mInputResource = InputSource(params[0]?.openStream())

            // 파싱
            mXMLReader.parse(mInputResource)

            // 링크로부터 본문&이미지 로딩
            for(idx in rssNewsLink.indices) { // 0<= idx <=rssNewsLink -1
                extractImageandDescFromLink(rssNewsLink[idx])
            }
        } catch (e : MalformedURLException) {
            e.printStackTrace()
        }
        catch (e : ParserConfigurationException) {
            e.printStackTrace()
        }
        catch (e : SAXException) {
            e.printStackTrace()
        }
        catch (e : IOException) {
            e.printStackTrace()
        }

        return "postexcute에 넘겨줘야할게 있다면 여기서 넘겨주자."
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: String?) {

        // @TODO = 이거 지금 안들어오는듯.
        val keywordList = keywordHelper.extractKeywordsFromJSON()

        for(idx in 0 until rssNewsNum) {
            Log.d("FIANLKEYWORD", "첫번 째 = " + keywordList[idx][0] + "두번 째 =" + keywordList[idx][1] + "세번 째 =" + keywordList[idx][2])
        }


        // 여기서 뉴스 리스트를 업데이트 시킨다.
        (context as MainActivity).updateRSSNewsList(rssNewsTitle, rssNewsLink,  rssNewsThumbnail, rssNewsDesc, rssNewsNum, keywordList)
        (context as MainActivity).swipeLayout.isRefreshing = false
        (context as MainActivity).swipeLayout.isEnabled = true

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
            if (Patterns.WEB_URL.matcher(link).matches()) {
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
        }

        rssNewsThumbnail.add(imgLink)
        rssNewsDesc.add(newsDesc)

        // 본문으로 부터 키워드를 뽑기 위해 TEXT에서 키워드 추출해주는 API를 이용해 JSONOBJECT를 뽑는다.
        keywordHelper.getJSONFromDesc(newsDesc)

    }
}
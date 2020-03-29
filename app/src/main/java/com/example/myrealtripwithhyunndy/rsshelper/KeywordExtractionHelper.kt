package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.URL
import java.net.URLEncoder
import java.util.Collections.sort

/*
MainActivity로 부터 뉴스 본문 내용을 전달 받아 키워드 3개를 추출해주는 Class.


ADAMS.ai의 키워드 추출 API 사용

http://api.adams.ai/datamixiApi/keywordextract?key=5174433123451770068&request_id=id&text=%22%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94%EC%95%88%EB%85%95%EC%95%88%EB%85%95%22

키워드 결과가 JSON으로 나오기 때문에 JSON 파싱이 필요하다.


return_object array에 term의 갯수 만큼 (term, weight) object를 갖고 있음.

 */

/*

{
return_type: "keywordextract",
result: "0",
reason: "Success",
return_object: [
{
term: "안녕안녕|0.3557950210422079",
weight: 0.3557950210422079
},
{
term: "안녕|0.005070525418223253",
weight: 0.005070525418223253
}
],
request_id: "id",
result_code: "success"
}

 */



class KeywordExtractionHelper {

    var descJSONList = ArrayList<String>()


    // 1. <키워드1, 키워드2, 키워드3>
    // 2. <키워드1, 키워드2, 키워드3>
    var keywordList = ArrayList<ArrayList<String>>()


    private val apiURL = "http://api.adams.ai/datamixiApi/keywordextract"
    private val keyValue = "5174433123451770068"

    // API를 이용해 본문에서 추출
    fun getJSONFromDesc(newsDesc: String) {

        var temp: String
        var rssNewsDesc = newsDesc.trim()

        if (rssNewsDesc == "......") {
            temp = "FailToGetAPI"
        } else {

            try {
                // 1. 요청 보낼 URL을 InputStream에 넣어준다. -> openStream()
                val encodedDesc = URLEncoder.encode(rssNewsDesc, "UTF-8")
                val stream = URL("$apiURL?key=$keyValue&text=$encodedDesc").openStream()

                // 2. BuffredReader를 이용해 inputStream에의 내용을 읽어온다.
                val inputStream = InputStreamReader(stream, "UTF-8")

                //3. inputStream이 char단위다.
                val reader = BufferedReader(inputStream)

                /*
                일반적으로 BufferedReader 를 통해 입력받을때 유의하셔야 할 점이 몇가지 있습니다.

                1. 기본적으로 BufferedReader는 한 줄을 통째로 입력받는 방법으로 주로 쓰입니다.

                2. readLine() 메서드는 값을 읽어올 때, String값으로 개행문자(엔터값)를 포함해 한줄을 전부 읽어오는 방식입니다.
                 */
                //4. 버퍼리더에 있는 text를 읽어오기.
                temp = reader.use(BufferedReader::readText)

                // Log.d("JSONOBJECT =", temp)
            } catch (e: FileNotFoundException) {

                temp = "FailToGetAPI"
                Log.d("JSONEXCEPTION", "API로부터 아예 값을 못읽어옴.")
            }
        }

        descJSONList.add(temp)
    }

    fun extractKeywordsFromJSON(): ArrayList<ArrayList<String>> {



        Log.d("FIANLKEYWORD", "JSONLIST갯수 = ${descJSONList.size}")


        // 1. 본문 내용으로 부터
        for (idx in 0..descJSONList.size) {

            // 1. 만약 위에서 json파일 얻는거 실패했으면 nokeyword로 넣기.
            try {
                if (descJSONList[idx] == "FailToGetAPI") {
                    var tempArray = arrayListOf("noKeyword", "noKeyword", "noKeyword")
                    keywordList.add(tempArray)
                    continue
                }

                val tempKeywordList = arrayListOf<String>()
                val tempWeightList = arrayListOf<Double>()
                // 2. jsonObjct 가져오기
                val json = JSONObject(descJSONList[idx])
                if (json.get("reason") == "Success") {
                    Log.d("FIANLKEYWORD", "$idx 번째 JSONOBJECT 파싱에 성공해서 안으로 들어옴")

                    val tempArray = json.getJSONArray("return_object")
                    var validTermNum = 0
                    for (termIdx in 0 until tempArray.length()) {
                        val obj = tempArray.getJSONObject(termIdx)

                        var tempKeyword = obj.getString("term")
                        if (!tempKeyword.contains(("_"))) {

                            // | 자르기
                            val targetIdx = tempKeyword.indexOf("|")
                            tempKeyword = tempKeyword.substring(0, targetIdx)
                            Log.d("FIANLKEYWORD", "$idx 번째 JSONOBJECT의 $termIdx 번째 용어 =$tempKeyword")
                            tempKeywordList.add(tempKeyword)

                            val tempWeight = obj.getDouble("weight")
                            tempWeightList.add(tempWeight)

                            validTermNum++
                            if (validTermNum == 3) {
                                break
                            }
                        }
                    }
                    keywordList.add(tempKeywordList)
                }
            } catch (e: Exception) {

                var tempArray = arrayListOf("noKeyword", "noKeyword", "noKeyword")
                keywordList.add(tempArray)
                Log.d("JSONEXCEPTION", "JSON 파서에서 매우 오류")
                e.printStackTrace()
            }
        }

        Log.d(
            "FIANLKEYWORD", "키워드리스트 갯수 = ${keywordList.size}"
        )

        return keywordList
    }
}
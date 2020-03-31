package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.Collections.sort
import kotlin.collections.ArrayList

/*
MainActivity로 부터 뉴스 본문 내용을 전달 받아 키워드 3개를 추출해주는 Class.


ADAMS.ai의 키워드 추출 API 사용

http://api.adams.ai/datamixiApi/keywordextract?key=5174433123451770068&request_id=id&text=%22%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94%EC%95%88%EB%85%95%EC%95%88%EB%85%95%22

키워드 결과가 JSON으로 나오기 때문에 JSON 파싱이 필요하다.


return_object array에 term의 갯수 만큼 (term, weight) object를 갖고 있음.

 */
class KeywordExtractionHelper {

    var descJSONList = ArrayList<String>()
    var keywordList = ArrayList<ArrayList<String>>()


    private val apiURL = "http://api.adams.ai/datamixiApi/keywordextract"
    private val keyValue = "5174433123451770068"

    // API를 이용해 본문에서 추출
    fun getJSONFromDesc(newsDesc: String) {

        var tempJSON: String
        var rssNewsDesc = newsDesc.trim()

        if (rssNewsDesc == "......") {
            tempJSON = "FailToGetAPI"
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
                tempJSON = reader.use(BufferedReader::readText)

            } catch (e: FileNotFoundException) {
                tempJSON = "FailToGetAPI"
                e.printStackTrace()
            }
        }

        descJSONList.add(tempJSON)
    }

    fun extractKeywordsFromJSON(): ArrayList<ArrayList<String>> {

        var noKeywordArray = arrayListOf("noKeyword", "noKeyword", "noKeyword")

        // 1. 본문 내용으로 부터
        for (idx in 0..descJSONList.size) {

            // 1. 만약 위에서 json파일 얻는거 실패했으면 nokeyword로 넣기.
            try {

                if (descJSONList[idx] == "FailToGetAPI") {
                    keywordList.add(noKeywordArray)
                    continue
                }

                var tempKeywordList = arrayListOf<String>()
                val tempWeightList = arrayListOf<Double>()

                // 2. jsonObjct 가져오기
                val json = JSONObject(descJSONList[idx])
                if (json.get("reason") == "Success") {
                    val tempArray = json.getJSONArray("return_object")
                    var validTermNum = 0
                    for (termIdx in 0 until tempArray.length()) {
                        val obj = tempArray.getJSONObject(termIdx)

                        var tempKeyword = extractTermFromJSON(obj)
                        if(tempKeyword != "noKeyword") {
                            tempKeywordList.add(tempKeyword)

                            val tempWeight = obj.getDouble("weight")
                            tempWeightList.add(tempWeight)

                            validTermNum++
                            if (validTermNum == 3)  break
                        }
                    }

                    // 문자가 같으면 해줘야함
                    tempKeywordList = checkSameWeight(tempKeywordList, tempWeightList)
                    keywordList.add(tempKeywordList)
                }
            } catch (e: Exception) {
                keywordList.add(noKeywordArray)
                e.printStackTrace()
            }
        }

        return keywordList
    }

    // JSON파일에서 키워드들을 추출한다.
    private fun extractTermFromJSON(obj : JSONObject) : String{

        var resultTerm  = "noKeyword"
        var tempTerm = obj.getString("term")

        // 추출 API에서 띄어쓰기된 단어를 _로 이어서 표시되는 경우가 존재하기 때문에 제외
        if(tempTerm != "" && !tempTerm.contains("_")) {

            // 추출 API 에서 Term에 term이름 | 가중치로 표시해서 전달해주기 때문에 | 기준으로 자른다.
            val targetIdx = tempTerm.indexOf("|")
            resultTerm = tempTerm.substring(0, targetIdx)

            if(resultTerm == "코로") {
                resultTerm = "코로나19"
            }

        }

        return resultTerm
    }

    // 만약 가중치가 같은 키워드가 있다면, 비교 후 오름차순으로 정렬
    private fun checkSameWeight(keywordList : ArrayList<String>, weightList : ArrayList<Double>) : ArrayList<String>{

        var resultKeywordList = keywordList

        // 0번과 1번
        if(weightList[0] == weightList[1]) {

            var t1 = keywordList[0]
            var t2 = keywordList[1]

            if(keywordList[0].compareTo(keywordList[1]) > 0) {
                var temp = keywordList[0]
                resultKeywordList[0] = keywordList[1]
                resultKeywordList[1] = temp
            }

        } else if(weightList[1] == weightList[2]) {

            var t1 = keywordList[1]
            var t2 = keywordList[2]
            if(keywordList[1].compareTo(keywordList[2]) > 0) {
                var temp = keywordList[1]
                resultKeywordList[1] = keywordList[2]
                resultKeywordList[2] = temp
            }

        } else if((weightList[0] == weightList[1]) && (weightList[1] == weightList[2])) {
            resultKeywordList.sort()
        }

        return resultKeywordList
    }
}
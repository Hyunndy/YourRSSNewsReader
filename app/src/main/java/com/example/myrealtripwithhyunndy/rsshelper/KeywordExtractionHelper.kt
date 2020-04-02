package com.example.myrealtripwithhyunndy.rsshelper

import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.URL
import java.net.URLEncoder
import kotlin.collections.ArrayList

/*
created by hyeonjiy 20.04.03

RSSHelper 클래스에서 얻은 뉴스 본문에서  https://www.adams.ai/apiPage?keywordextract 키워드 추출 api를 이용해 키워드 3개를 추출하는 클래스.
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
                val encodedDesc = URLEncoder.encode(rssNewsDesc, "UTF-8")
                val stream = URL("$apiURL?key=$keyValue&text=$encodedDesc").openStream()

                val inputStream = InputStreamReader(stream, "UTF-8")
                val reader = BufferedReader(inputStream)

                tempJSON = reader.use(BufferedReader::readText)

            } catch (e: FileNotFoundException) {
                tempJSON = "FailToGetAPI"
                e.printStackTrace()
            }
        }

        descJSONList.add(tempJSON)
    }

    fun extractKeywordsFromJSON(): ArrayList<ArrayList<String>> {

        var noKeywordArray = arrayListOf("", "", "")

        for (idx in 0..descJSONList.size) {
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
                        if(tempKeyword != "") {
                            tempKeywordList.add(tempKeyword)

                            val tempWeight = obj.getDouble("weight")
                            tempWeightList.add(tempWeight)

                            validTermNum++
                            if (validTermNum == 3)  break
                        }
                    }
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

    // api에서 반환하는 문자열에서 키워드를 추출하는 함수.
    private fun extractTermFromJSON(obj : JSONObject) : String{

        var resultTerm  = ""
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
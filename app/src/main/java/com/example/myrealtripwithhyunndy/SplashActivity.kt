package com.example.myrealtripwithhyunndy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.myrealtripwithhyunndy.rsshelper.RSSHelper

/*
마이리얼트립 과제
~RSS 뉴스 리더 어플리케이션~

기능1. 스플래시 화면

1-1) 1.3초 후에 뉴스 리스트 화면으로 이동

1-2) 화면 구성
1. 로고 이미지(원형)
- 가운데 정렬 = https://www.charlezz.com/?p=669
- 1:1 비율 사이즈
- 원형 마스킹

2. 좌우 작은 이미지
- 로고 이미지 대비 1/3 사이즈
- 1:1 비율 사이즈
- 로고 이미지와 하단 정렬, 화면 좌우 동일 마진

3. 앱 설명 텍스트
- 라벨 3개
- 왼쪽 정렬
- 멀티라인
- 텍스트 영역 전체 화면 가운데 정렬
 */

class SplashActivity : AppCompatActivity() {

    // 1.3간 스플래시 화면을 보여줌
    private val SPLASH_REMAIN_TIME: Long = 1300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            }, SPLASH_REMAIN_TIME)
    }
}

package com.example.myrealtripwithhyunndy

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

/*
마이리얼트립 과제
~RSS 뉴스 리더 어플리케이션~

기능1. 스플래시 화면
( 앱 실행 전에 나오는 화면 )
1-1) Branded launch screens로 구현
1-2) 1.3초 후에 뉴스 리스트 화면으로 이동
1-3) 화면 구성
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

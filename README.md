# myrealtripwithhyunndy

@TODO : JSoup 사용 부분 서버단으로 넘겨서 구현할 수 있게 적용이 필요(for 시간단축)

마이리얼트립 챌린지
~RSS 뉴스 리더 어플리케이션~

기능1. 스플래시 화면(SplashActivity에 구현)

1-1) 1.3초 후에 뉴스 리스트 화면으로 이동

1-2) 화면 구성
1. 로고 이미지(원형)
- 가운데 정렬
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

기능 2. 뉴스 리스트

2-1) 아이템 구성(MainActivity)
1. 썸네일, 제목, 본문의 일부, 주요 키워드 3개
2. 각 뉴스 항목(item)을 선택하면 뉴스 상세보기 화면으로 넘어갈 수 있게
3. 당기면 리스트 업데이트

2-2) RSS 에서 가져오기
- RSSHelper클래스에서 SXA Parser를 이용해 XML 파싱

2-3) 키워드 추출
- KeywordExtractionHelper 클래스에서 https://www.adams.ai/apiPage?keywordextract 키워드 추출 api를 이용해 키워드 추출.

 기능 3. 뉴스 상세화면
 - DetailNewsActivity에서 webView로 나오도록 구현.

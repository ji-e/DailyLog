# DailyLog

### 1. 목적
입사초기에 월간일지를 작성하기 위해 만든 안드로이드 애플리케이션

- Kotlin으로 안드로이드 애플리케이션을 구현하는 방법을 연습
- SQLite 구현 연습
- poi라이브러리를 이용한 엑셀관련 기능 구현 연습


### 2. 개발환경
- Language: Kotlin
- DB: SQLite
- Tool: AndroidStudio, GitHub


### 3. 기능
####  1) 메인
- 사용자에 따라 일간/주간/월간으로 설정
- 일간: 
  - ListView로 구현
  - 일별로 일지 리스트를 보여주며, 드롭다운버튼을 통해 상세내용 확인
- 주간:
  - ListView로 구현
  - 주별로 일지 리스트를 보여주며, 날짜 클릭시 그 날의 일간 일지 리스트로 화면으로 이동
- 월간: 
  - GridView로 구현
  - 월별로 일지 리스트를 보여주며, 랜덤의 동그라미 표시로 일지의 존재 여부를 확인.
  - 날짜 클릭 시, 하단 리스트에 등록된 일지를 확인.

####  2) 일지 등록
  - SQLiteOpenHelper를 이용한 DB생성
  - 제목 및 상세내용을 작성하여 일지 등록
  - 최신 등록한 10개의 리스트 불러오기 가능
  
####  3) 일지 검색
  - 일지 명으로 검색
    - 일지 명 및 날짜 기간을 선택하여 일지 검색
  - 날짜이동
    - 캘린더 다이얼로그에서 날짜 선택 시, 선택한 날짜의 일지 검색
    
#### 4) 환경설정
 - 비밀번호 설정
   - 6자리의 핀 비밀번호을 등록하면 앱 구동시 비밀번호를 입력해야 메인으로 이동
 - 엑셀 가져오기/ 엑셀 내보내기
   - poi라이브러리로 구현
   
    1. 엑셀파일 만들기
    <pre>
    <code>
    val headerFont = workbook.createFont()
    headerFont.bold = true
    headerFont.boldweight = (Font.BOLDWEIGHT_BOLD)
    headerFont.color = IndexedColors.BLUE_GREY.getIndex()
    
    val headerCellStyle = workbook.createCellStyle()
    headerCellStyle.setFont(headerFont)
    
    val headerRow = sheet.createRow(0)
    for (i in 0 until columns.size) {
      var cell = headerRow.createCell(i)
      cell.setCellValue(columns[i])
      cell.cellStyle = headerCellStyle
    }
    </pre>
    </code>

    2. 엑셀시트에 데이터 삽입
    <pre>
    <code>
    val dateCellStyle = workbook.createCellStyle()
    dateCellStyle.dataFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd")

    var rowNum = 1

    for (data in dataList) {
      val row = sheet.createRow(rowNum++)
      // no
      row.createCell(0).setCellValue(data.no.toString())
      // date
      val dateCell = row.createCell(1)
      dateCell.setCellValue(data.date.toString())
      dateCell.cellStyle = dateCellStyle
      //title
      row.createCell(2).setCellValue(data.title)
      //content
      row.createCell(3).setCellValue(data.content)
    }

    sheet.setColumnWidth(1, 3000)
    sheet.setColumnWidth(2, 5000)
    </pre>
    </code>
    
    3. 엑셀파일 쓰기
    <pre>
    <code>
     val fileout = FileOutputStream(totalName)
     workbook.write(fileout)
     fileout.close()
    </pre>
    </code>
    
    4. 엑셀파일 읽기
    <pre>
    <code>
    val excelFile = FileInputStream(File(totalName))
    val workbook = XSSFWorkbook(excelFile)
    val sheet = workbook.getSheet("dlog_sheet")

    for (rowIndex in 1 until sheet.physicalNumberOfRows) {
        val row = sheet.getRow(rowIndex)
        if (row != null) {
           var no: String = ""
           var date: String = ""
           var title: String = ""
           var content: String = ""
           for (columnIndex in 0 until row.physicalNumberOfCells) {
               val cell = row.getCell(columnIndex)
               if (cell != null) {
                  when (columnIndex) {
                      0 -> no = cell.stringCellValue
                      1 -> date = cell.stringCellValue
                      2 -> title = cell.stringCellValue
                      3 -> content = cell.stringCellValue
                   }
               }
            }
            dataList.add(DBData(no.toInt(), date.toInt(), title, content))
            LogUtil.d(date)
       }
    }
    excelFile.close()
    </pre>
    </code>
    
    
#### 5) 생각 및 정리
처음으로 코틀린으로 구현한 안드로이드 앱이라 아직 부족한 점이 많다.    
구조도 갖춰져 있지 않으며, 자바를 코틀린으로 바꾼형태로 코틀린를 잘 활용하지 못해 아쉬움이 남는다.   
추후에 디자인과 구조를 바꾸어 더욱 매끄러운 앱을 구현해야겠다.     

  





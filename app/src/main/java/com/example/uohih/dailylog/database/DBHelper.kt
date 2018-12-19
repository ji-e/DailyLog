package com.example.uohih.dailylog.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.uohih.dailylog.base.LogUtil

class DBHelper(mcontext: Context) : SQLiteOpenHelper(mcontext, "dlog", null, 1) {

    /**
     * 칼럼
     * no: primary key to autoincrement
     * date: yyyymmdd
     * title: 제목
     * content: 내용
     */
    val tableName = "tb_dlog"


    override fun onCreate(db: SQLiteDatabase) {
        val queryCreate = "create table $tableName (no integer primary key autoincrement, date integer, title, content)"
        db.execSQL(queryCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val queryDrop = "drop table $tableName"
        db.execSQL(queryDrop)
    }


    /**
     * tb_dlog 초기화
     */
    fun onReset(){
        val db = writableDatabase
        val queryDrop = "drop table $tableName"
        db.execSQL(queryDrop)
        val queryCreate = "create table $tableName (no integer primary key autoincrement, date integer, title, content)"
        db.execSQL(queryCreate)
        db.close()
    }

    /**
     * tb_dlog에 데이터 삽입
     */
    fun insert(date: Int, title: String, content: String) {
        val db = writableDatabase
        val queryInsert = "insert into $tableName (date, title, content) values (?,?,?), ($date, \'$title\', \'$content\')"
        db.execSQL(queryInsert)
        db.close()
        LogUtil.d(queryInsert)
    }

    /**
     * tb_dlog의 데이터 수정
     */
    fun update(no: Int, title: String, content: String) {
        val db = writableDatabase
        val queryUpdate = "update $tableName set title =\'$title\', content=\'$content\' where no=\'$no\'"
        db.execSQL(queryUpdate)
        db.close()
        LogUtil.d(queryUpdate)
    }

    /**
     * tb_dlog의 데이터 삭제
     */
    fun delete(array: ArrayList<String>, index: String) {
        val db = writableDatabase
        for (i in 0 until array.size) {
            val no = array[i]
//            var queryDelete = "delete from $tableName where $index=\'$no\'"
//            if(index.equals("no")){
            var queryDelete = "delete from $tableName where $index=$no"
//            }
            db.execSQL(queryDelete)
            LogUtil.d(queryDelete)
        }
        db.close()
    }

    /**
     * tb_dlog의 데이터 검색
     */
    fun select(date: Int): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date=\'$date\'"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(first: Int, last: Int): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date>=\'$first\' and date<=\'$last\' order by date asc"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(first: Int, last: Int, str:String): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date>=\'$first\' and date<=\'$last\' and title like \'%$str%\' order by date asc "
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(str:String): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where title like \'%$str%\' order by date desc"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun selectAll(): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun selectResent(): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName order by date desc limit 10"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }


}
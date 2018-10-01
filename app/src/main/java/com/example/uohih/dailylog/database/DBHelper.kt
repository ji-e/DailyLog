package com.example.uohih.dailylog.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(mcontext: Context) : SQLiteOpenHelper(mcontext, "dlog", null, 1) {

    /**
     * 칼럼
     * no: primary key to autoincrement
     * date: yyyymmdd
     * title: 제목
     * content: 내용
     */
    val tag="DBHelper"
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
     * tb_dlog에 데이터 삽입
     */
    fun insert(date: Int, title: String, content: String) {
        val db = writableDatabase
        val queryInsert = "insert into $tableName (date, title, content) values (?,?,?), ($date, \'$title\', \'$content\')"
        db.execSQL(queryInsert)
        db.close()
        Log.d(tag,queryInsert)
    }

    /**
     * tb_dlog의 데이터 수정
     */
    fun update(no:Int, title: String, content: String) {
        val db = writableDatabase
        val queryUpdate = "update $tableName set title =\'$title\', content=\'$content\' where no=\'$no\'"
        db.execSQL(queryUpdate)
        db.close()
        Log.d(tag,queryUpdate)
    }

    /**
     * tb_dlog의 데이터 삭제
     */
    fun delete(no: Int) {
        val db = writableDatabase
        val queryDelete = "delete $tableName where no=\'$no\'"
        db.execSQL(queryDelete)
        db.close()
        Log.d(tag,queryDelete)
    }

    /**
     * tb_dlog의 데이터 검색
     */
    fun select (date: Int) :Cursor {
        val db = writableDatabase
        val querySelect ="select * from $tableName where date=\'$date\'"
        db.rawQuery(querySelect,null)
        Log.d(tag,querySelect)
        return db.rawQuery(querySelect,null)
    }

}
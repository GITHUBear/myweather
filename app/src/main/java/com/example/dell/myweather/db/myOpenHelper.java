package com.example.dell.myweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dell on 2016/10/1.
 */

public class myOpenHelper extends SQLiteOpenHelper{
    //为Province建表
    public static final String PROVINCE="create table Province("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";
    //为City建表
    public static final String City="create table City("
            +"id integer primary key autoincrement,"
            +"city_name text,"
            +"city_Code text,"
            +"province_id Integer)";
    //为County建表
    public static final String County="create table County("
            +"id integer primary key autoincrement,"
            +"county_name text,"
            +"county_code text,"
            +"city_id integer)";
    public  myOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(PROVINCE);
        db.execSQL(City);
        db.execSQL(County);
    }
    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
    }
}

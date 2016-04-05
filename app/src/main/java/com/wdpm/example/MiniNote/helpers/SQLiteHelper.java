package com.wdpm.example.MiniNote.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wdpm.example.MiniNote.model.Note;

/**
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sqLiteHelper = null;
    private static final String DATABASE_NAME = "NoteForest.db";
    private static final int DATABASE_VERSION= 6;
    private static String CLASS_NAME;

    //懒汉单例模式，没有加同步锁，线程不安全
    public static SQLiteHelper getInstance(Context context){
        if(sqLiteHelper == null){
            sqLiteHelper = new SQLiteHelper(context);
        }
        return sqLiteHelper;
    }

    protected SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.CLASS_NAME = getClass().getName();
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        Log.d(CLASS_NAME, "onCreate()");
        Note.createTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.d(CLASS_NAME, "onUpgrade()");
        Note.dropTable(database);
        onCreate(database);
    }

    //外键约束
    @Override
    public void onConfigure(SQLiteDatabase database){
        database.setForeignKeyConstraintsEnabled(true);
    }

    public void create(){
        Log.d(CLASS_NAME, "create()");
        getWritableDatabase();
    }
}

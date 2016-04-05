package com.wdpm.example.MiniNote.model;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wdpm.example.MiniNote.helpers.SQLiteHelper;

/**
 * Created by Greg Christopherson on 10/1/2015.
 */
public class Note {

    private Long _id = null;
    private Long date_created;
    private Integer appWidgetId;
    private String note_title;
    private String note_body;
    private SQLiteHelper helper;
    private int primaryPreselect;
    private int accentPreselect;
    private int fontSize;

    private static String CLASS_NAME;

    public Note(SQLiteHelper helper){
        this.CLASS_NAME = getClass().getName();
        this.helper = helper;
    }

    public static void createTable(SQLiteDatabase database){
        Log.d(CLASS_NAME, "createTable()");
        String sql = "CREATE TABLE IF NOT EXISTS notes "
                +"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"app_widget_id INTEGER NOT NULL, "
                +"date_created INTEGER NOT NULL, "
                +"note_title TEXT, "
                +"note_body TEXT, "
                +"primaryPreselect INTEGER, "
                +"accentPreselect INTEGER, "
                +"fontSize INTEGER);";


        database.execSQL(sql);
    }

    public static void dropTable(SQLiteDatabase database){
        Log.d(CLASS_NAME, "dropTable()");
        String sql = "DROP TABLE IF EXISTS notes;";
        database.execSQL(sql);
    }

    public void saveNote(Integer appWidgetId, String title, String body ,int primaryPreselect,int accentPreselect,int fontSize){

        SQLiteDatabase database = helper.getWritableDatabase();
        String sql;

        //新建widget
        if(getNote(appWidgetId) == null){
            date_created = System.currentTimeMillis();
            Log.d(CLASS_NAME, "Creating new row");
            sql = "INSERT INTO notes (app_widget_id, date_created, note_title, note_body, primaryPreselect, " +
                    "accentPreselect, fontSize) VALUES ("
                    +appWidgetId + ", "
                    +date_created + ", '"
                    +title + "', '"
                    +body +  "', '"
                    +primaryPreselect + "', '"
                    +accentPreselect + "', '"
                    +fontSize +"');";

        } else {//更新已有的widget
            Log.d(CLASS_NAME, "updating row");
            sql = "UPDATE notes SET "
                    +"note_title = '"+title + "', "
                    +"note_body = '"+body + "', "
                    +"primaryPreselect = '"+primaryPreselect + "', "
                    +"accentPreselect = '"+accentPreselect + "', "
                    +"fontSize = '"+fontSize + "' "
                    +"WHERE app_widget_id = " + appWidgetId
                    +";";
        }
        Log.d(CLASS_NAME, "Date created: " + date_created.toString());
        Log.d(CLASS_NAME, "Widget ID: " + appWidgetId);
        Log.d(CLASS_NAME, "UPDATE notes SET "
                +"note_title = '"+title + "', "
                +"note_body = '"+body + "' "
                +"primaryPreselect = '"+primaryPreselect + "' "
                +"accentPreselect = '"+accentPreselect + "' "
                +"fontSize = '"+fontSize + "' "
                +"WHERE app_widget_id = " + appWidgetId
                +";");
        database.execSQL(sql);
    }

    public Note getNote(Integer appWidgetId){
        this.appWidgetId = appWidgetId;
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM notes WHERE app_widget_id = " + appWidgetId + ";", null);
        cursor.moveToFirst();

        try {
            this.date_created = cursor.getLong(cursor.getColumnIndex("date_created"));
            this.note_title = cursor.getString(cursor.getColumnIndex("note_title"));
            this.note_body = cursor.getString(cursor.getColumnIndex("note_body"));
            this.primaryPreselect = cursor.getInt(cursor.getColumnIndex("primaryPreselect"));
            this.accentPreselect = cursor.getInt(cursor.getColumnIndex("accentPreselect"));
            this.fontSize = cursor.getInt(cursor.getColumnIndex("fontSize"));
        } catch (CursorIndexOutOfBoundsException x){
            Log.d(CLASS_NAME, "Tried to find a note that wasn't there.");
            cursor.close();
            return null;
        }
        cursor.close();
        return this;
    }

    public boolean isNoteNull(){
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM notes ;", null);
        if (cursor == null){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public String toString(){
        return note_title+"\n"+note_body;
    }

    public Long get_id() {
        return _id;
    }

    public Long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public String getNote_body() {
        return note_body;
    }

    public void setNote_body(String note_body) {
        this.note_body = note_body;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public Integer getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(Integer appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getAccentPreselect() {
        return accentPreselect;
    }

    public void setAccentPreselect(int accentPreselect) {
        this.accentPreselect = accentPreselect;
    }

    public int getPrimaryPreselect() {
        return primaryPreselect;
    }

    public void setPrimaryPreselect(int primaryPreselect) {
        this.primaryPreselect = primaryPreselect;
    }

}

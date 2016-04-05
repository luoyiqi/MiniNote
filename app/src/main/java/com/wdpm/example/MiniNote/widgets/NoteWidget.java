package com.wdpm.example.MiniNote.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

import com.wdpm.example.MiniNote.helpers.SQLiteHelper;
import com.wdpm.example.MiniNote.helpers.WidgetHelper;
import com.wdpm.example.MiniNote.model.Note;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    private static final String TAG ="NoteWidget" ;
    private static String CLASS_NAME;
    private Note note;

    public NoteWidget(){
        this.CLASS_NAME = getClass().getName();
    }

    //第一次是调用configure acitivity，跳过此方法。
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Log.d(TAG,"onUpdate");
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            System.out.println("onUpdate() widgetId: " + appWidgetIds[i]);
            if(note == null){
                onEnabled(context);
            }
            note = note.getNote(appWidgetIds[i]);
            if(note != null){
                WidgetHelper.updateWidget(context, appWidgetIds[i], note.getNote_title(), note.getNote_body(),
                        note.getPrimaryPreselect(), note.getAccentPreselect(), note.getFontSize());
            }
        }
    }

    //创建第一个widget时调用
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(CLASS_NAME, "onEnabled()");
        SQLiteHelper helper = SQLiteHelper.getInstance(context);
        helper.create();//调用getWritableDatabase();
        note = new Note(helper);//得到一个helper实例
    }

    //最后一个widget删除时调用
    @Override
    public void onDisabled(Context context) {
        Log.d(CLASS_NAME, "onDisabled()");
        // Enter relevant functionality for when the last widget is disabled
    }
}


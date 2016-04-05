package com.wdpm.example.MiniNote.helpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.wdpm.example.MiniNote.R;
import com.wdpm.example.MiniNote.utils.ImageUtil;
import com.wdpm.example.MiniNote.activities.EditNoteActivity;
import com.wdpm.example.MiniNote.utils.ScreenUtil;

/**
 *
 */
public class WidgetHelper {

    private static final String TAG = "WidgetHelper";
    private static String CLASS_NAME;

    public WidgetHelper() {
        CLASS_NAME = getClass().getName();
    }

    public static void updateWidget(Context context, Integer widgetId, String title, String body, int primaryPreselect, int accentPreselect, int fontSize) {
        Log.d(TAG, "updateWidget()");
        Log.d(TAG + "getPackage:", context.getPackageName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget);

        views.setTextViewText(R.id.widget_title_text, title);
        views.setTextViewText(R.id.widget_body_text, body);

        views.setTextColor(R.id.widget_title_text, accentPreselect);
        views.setTextColor(R.id.widget_body_text, accentPreselect);

        views.setTextViewTextSize(R.id.widget_title_text, TypedValue.COMPLEX_UNIT_SP, fontSize);
        views.setTextViewTextSize(R.id.widget_body_text, TypedValue.COMPLEX_UNIT_SP, fontSize);

        int screenWidth = ScreenUtil.getScreenWidth(context);
        int screenHeight = ScreenUtil.getScreenHeight(context);
        Bitmap background= ImageUtil.getPureBackgroundBitmap(primaryPreselect,screenWidth,screenHeight);
        views.setImageViewBitmap(R.id.iv,background);

        setPendingIntent(context, views, widgetId);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    //pendingIntent是等待意图，不会立即执行，点击时才会执行
    private static void setPendingIntent(Context context, RemoteViews views, Integer widgetId) {
        Log.d(TAG, "setPendingIntent()");
        Intent intent = new Intent(context.getApplicationContext(), EditNoteActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), widgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.note_widget, pendingIntent);
    }
}

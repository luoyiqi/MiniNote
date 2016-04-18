package com.wdpm.example.MiniNote.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.wdpm.example.MiniNote.BuildConfig;
import com.wdpm.example.MiniNote.R;
import com.wdpm.example.MiniNote.helpers.SQLiteHelper;
import com.wdpm.example.MiniNote.helpers.WidgetHelper;
import com.wdpm.example.MiniNote.model.Note;
import com.wdpm.example.MiniNote.utils.Constant;
import com.wdpm.example.MiniNote.utils.SDCardUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EditNoteActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    private static final String TAG = "EditNoteActivity";
    private static String CLASS_NAME;
    private SQLiteHelper sqLiteHelper;
    private Integer mAppWidgetId;
    private String noteTitle;
    private String noteBody;
    private LinearLayout linearLayout;

    // color chooser dialog
    private int primaryPreselect;
    private int accentPreselect;

    //font size
    private int fontSize = 20;//默认大小

    private File backupPath;
    private MaterialDialog backUpDialog;
    public static String BACKUP_FILE_NAME = "note_NUMBER.txt"; // Backup file name

    public EditNoteActivity() {
        this.CLASS_NAME = getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "onCreate()");
        super.onCreate(savedInstanceState);
        this.setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_note);
        //init color palette
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_edit_note);
        this.sqLiteHelper = SQLiteHelper.getInstance(getApplicationContext());
        Note note = new Note(sqLiteHelper);

        //视图初始化,加载之前便签内容
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            if (this.mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                Log.d(CLASS_NAME, "Valid AppWidgetID: " + this.mAppWidgetId);
                note = note.getNote(this.mAppWidgetId);
                if (note != null) {
                    EditText title = (EditText) findViewById(R.id.main_edit_title_text);
                    EditText body = (EditText) findViewById(R.id.main_edit_text);
                    title.setText(note.getNote_title(), TextView.BufferType.EDITABLE);
                    body.setText(note.getNote_body(), TextView.BufferType.EDITABLE);
                }
            } else {
                //invalid app widget id, exit.
                finish();
            }
        }
    }

    //更改便签内容后保存到数据库，同时返回意图结果
    public void saveNote(View view) {
        Log.d(CLASS_NAME, "saveNote()");
        Log.d(CLASS_NAME, "appWidgetId: " + mAppWidgetId);
        Note note = new Note(sqLiteHelper);
        noteTitle = ((EditText) findViewById(R.id.main_edit_title_text)).getText().toString();
        noteBody = ((EditText) findViewById(R.id.main_edit_text)).getText().toString();
        Log.d(CLASS_NAME, noteBody);

        //要添加三个字段，分别表示背景颜色、字体大小、字体颜色
        note.saveNote(mAppWidgetId, noteTitle, noteBody, primaryPreselect, accentPreselect, fontSize);
        //更新widget,到这里已经执行完widget配置
        updateWidget(mAppWidgetId, noteTitle, noteBody, primaryPreselect, accentPreselect, fontSize);
        //返回结果
        returnResult();
        finish();
    }

    //同时返回意图结果
    private void returnResult() {
        Log.d(CLASS_NAME, "returnResult()");
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    public void updateWidget(Integer widgetId, String title, String body, int primaryPreselect, int accentPreselect, int fontSize) {
        WidgetHelper.updateWidget(this, widgetId, title, body, primaryPreselect, accentPreselect, fontSize);
    }

    private void setPendingIntent(RemoteViews views) {
        Log.d(CLASS_NAME, "setPendingIntent()");
        Intent intent = new Intent(this.getApplicationContext(), this.getClass());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.mAppWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), this.mAppWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.note_widget, pendingIntent);
    }

    @Override
    public void onPause() {
        Log.d(CLASS_NAME, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(CLASS_NAME, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(CLASS_NAME, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.background_color_settings:
                showBackgroundColorChooser();
                return true;
            case R.id.font_color_settings:
                showFontColorChooser();
                return true;
            case R.id.font_size_settings:
                showFontSizeChooser();
                return true;
            case R.id.save_to_SDCard:
                showBackUpDialog();

        }

        return super.onOptionsItemSelected(item);
    }

    private void showBackUpDialog() {
         backUpDialog=new MaterialDialog.Builder(this)
                .title(R.string.save_to_SDCard)
                .content("确定要备份便签内容到SDCard吗")
                .negativeText("取消")
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Note notes=new Note(sqLiteHelper);
                        if (!notes.isNoteNull()) {
                            boolean backupSuccessful = saveNoteToSDCard();

                            if (backupSuccessful)
                                showBackupSuccessfulDialog();

                            else {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.toast_backup_failed),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        // If notes array is empty -> toast backup no notes found
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.toast_backup_no_notes),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }


                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();
                backUpDialog.show();
    }

    private boolean saveNoteToSDCard() {

        File backupFolder = new File(Environment.getExternalStorageDirectory() +
                Constant.BACKUP_FOLDER_PATH);

        if (SDCardUtil.isSDCardEnable() && !backupFolder.exists()){
            backupFolder.mkdir();
        }
        Note note=new Note(sqLiteHelper);
        String backUpPathReplace=BACKUP_FILE_NAME.replace("NUMBER",mAppWidgetId+"");
        backupPath = new File(backupFolder, backUpPathReplace);
        try {
            FileWriter fileWriter=new FileWriter(backupPath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            //将所有Note的内容写到txt
            bufferedWriter.write(((EditText) findViewById(R.id.main_edit_title_text)).getText().toString() + "\n" +
                                 ((EditText) findViewById(R.id.main_edit_text)).getText().toString());
            //将缓冲区内容强制写出
            bufferedWriter.flush();
            bufferedWriter.close();
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showBackupSuccessfulDialog() {
        backUpDialog.dismiss();
        // Dialog to display backup was successfully created in backupPath
        AlertDialog backupOKDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_backup_created_title)
                .setMessage(getString(R.string.dialog_backup_created) + " "
                        + backupPath.getAbsolutePath())
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        backupOKDialog.show();
    }

    private void showFontSizeChooser() {
        final int[] fonSizeTemp = {20};
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("选择字体大小")
                .customView(R.layout.ajust_font_size, false)
                .positiveText(R.string.confirm)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fontSize=fonSizeTemp[0];
                        Toast.makeText(dialog.getContext(),"字体大小为"+fonSizeTemp[0],Toast.LENGTH_SHORT).show();
                    }
                }).build();
        final TextView textView= (TextView) dialog.getCustomView().findViewById(R.id.text_view_font_size);
        final SeekBar seekBar = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress(20);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              Log.d("seekBar", String.valueOf(seekBar.getProgress()));
                fonSizeTemp[0] =seekBar.getProgress();
            }
        };
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        dialog.show();
    }

    private void showFontColorChooser() {
                new ColorChooserDialog.Builder(this, R.string.choose_font_color)
                .titleSub(R.string.font_color)
                .accentMode(true)
                .preselect(accentPreselect)
                .show();
    }

    private void showBackgroundColorChooser() {
        new ColorChooserDialog.Builder(this, R.string.choose_background_color)
                .titleSub(R.string.background_color)
                .preselect(primaryPreselect)
                .show();
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            try {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy
                        .Builder()
                        .detectLeakedClosableObjects()
                        .detectLeakedSqlLiteObjects()
                        .setClassInstanceLimit(Class.forName("com.notewidgets.appforest.notewidgets.activities.EditNoteActivity"), 100)
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        Log.e("onColorSelection", String.valueOf(selectedColor));
        if (dialog.isAccentMode()) {
            //强调色为字体颜色
            accentPreselect = selectedColor;
            EditText main_edit_text = (EditText) findViewById(R.id.main_edit_text);
            EditText main_edit_title_text = (EditText) findViewById(R.id.main_edit_title_text);
//            main_edit_text.setTextColor(accentPreselect);
//            main_edit_title_text.setTextColor(accentPreselect);
        } else {
            //主色调为背景颜色
            primaryPreselect = selectedColor;
//            linearLayout.setBackgroundColor(primaryPreselect);
        }
    }
}

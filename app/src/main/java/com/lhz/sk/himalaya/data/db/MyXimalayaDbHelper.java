package com.lhz.sk.himalaya.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;

import androidx.annotation.Nullable;

import com.lhz.sk.himalaya.utils.Contants;

import static com.lhz.sk.himalaya.utils.Contants.DB_NAME;
import static com.lhz.sk.himalaya.utils.Contants.DB_VERSION;

/**
 * Created by song
 */
public class MyXimalayaDbHelper extends SQLiteOpenHelper {
    public MyXimalayaDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        //订阅相关的字段
        //图片、title、描述、播放量、节目数量、作者名称（详情界面）专辑id
        String subTbSql = "create table " + Contants.SUB_TB_NAME + "(" +
                Contants.SUB_ID + " integer primary key autoincrement, " +
                Contants.SUB_COVER_URL + " varchar, " +
                Contants.SUB_TITLE + " varchar," +
                Contants.SUB_DESCRIPTION + " varchar," +
                Contants.SUB_PLAY_COUNT + " integer," +
                Contants.SUB_TRACKS_COUNT + " integer," +
                Contants.SUB_AUTHOR_NAME + " varchar," +
                Contants.SUB_ALBUM_ID + " integer" +
                ")";
        db.execSQL(subTbSql);
        //创建历史记录表
        String historyTbSql = "create table " + Contants.HISTORY_TB_NAME + "(" +
                Contants.HISTORY_ID + " integer primary key autoincrement, " +
                Contants.HISTORY_TRACK_ID + " integer, " +
                Contants.HISTORY_TITLE + " varchar," +
                Contants.HISTORY_COVER + " varchar," +
                Contants.HISTORY_PLAY_URL+" varchar," +
                Contants.HISTORY_PLAY_COUNT + " integer," +
                Contants.HISTORY_DURATION + " integer," +
                Contants.HISTORY_AUTHOR + " varchar," +
                Contants.HISTORY_KIND + " varchar," +
                Contants.HISTORY_UPDATE_TIME + " integer" +
                ")";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

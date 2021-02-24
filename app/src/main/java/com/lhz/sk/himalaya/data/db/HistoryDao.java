package com.lhz.sk.himalaya.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.utils.Contants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class HistoryDao implements IHistoryDao {

    private final MyXimalayaDbHelper mXimalayaDbHelper;
    private IHistoryDaoCallback mCallback = null;
    private Object mLock = new Object();

    public HistoryDao() {
        mXimalayaDbHelper = new MyXimalayaDbHelper(BaseApplication.getContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isaddHistory = false;
            try {
                db = mXimalayaDbHelper.getWritableDatabase();
                db.delete(Contants.HISTORY_TB_NAME, Contants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(Contants.HISTORY_TRACK_ID, track.getDataId());
                values.put(Contants.HISTORY_TITLE, track.getTrackTitle());
                values.put(Contants.HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(Contants.HISTORY_DURATION, track.getDuration());
                values.put(Contants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(Contants.HISTORY_COVER, track.getCoverUrlLarge());
                values.put(Contants.HISTORY_AUTHOR, track.getAnnouncer().getNickname());
                db.insert(Contants.HISTORY_TB_NAME, null, values);
                db.setTransactionSuccessful();
                isaddHistory = true;
            } catch (Exception e) {
                isaddHistory = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                mCallback.onAddHistory(isaddHistory);
            }

        }

    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mXimalayaDbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Contants.HISTORY_TB_NAME, Contants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onDelHistory(isDeleteSuccess);
                }
            }
        }

    }

    @Override
    public void clearHistory() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mXimalayaDbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Contants.HISTORY_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onClearHistory(isDeleteSuccess);
                }
            }
        }

    }

    @Override
    public void getHistorys() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try {
                db = mXimalayaDbHelper.getWritableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(Contants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Contants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Contants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Contants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Contants.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Contants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(Contants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);
                    String author = cursor.getString(cursor.getColumnIndex(Contants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryLoader(histories);
                }
            }
        }

    }
}

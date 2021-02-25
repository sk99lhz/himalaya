package com.lhz.sk.himalaya.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import com.lhz.sk.himalaya.bases.BaseApplication;

import com.lhz.sk.himalaya.utils.Contants;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

import static com.lhz.sk.himalaya.utils.Contants.SUB_ALBUM_ID;
import static com.lhz.sk.himalaya.utils.Contants.SUB_TB_NAME;

/**
 * Created by song
 */
public class SubscriptionDao implements ISubDao {

    private static final String TAB = "SubscriptionDao";
    private final MyXimalayaDbHelper mXimalayaDbHelper;
    private ISubDaoCallBack mCallBack = null;

    private SubscriptionDao() {
        mXimalayaDbHelper = new MyXimalayaDbHelper(BaseApplication.getContext());
    }

    public static SubscriptionDao Instance;

    public static SubscriptionDao getInstance() {
        if (Instance == null) {
            synchronized (SubscriptionDao.class) {
                if (Instance == null) {
                    Instance = new SubscriptionDao();
                }
            }
        }
        return Instance;
    }

    @Override
    public void setCallBack(ISubDaoCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void addAlbum(Album album) {
        boolean isAlbum = false;
        SQLiteDatabase db = null;
        try {
            db = mXimalayaDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            //封装数据
            contentValues.put(Contants.SUB_COVER_URL, album.getCoverUrlLarge());
            contentValues.put(Contants.SUB_TITLE, album.getAlbumTitle());
            contentValues.put(Contants.SUB_DESCRIPTION, album.getAlbumIntro());
            contentValues.put(Contants.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            contentValues.put(Contants.SUB_PLAY_COUNT, album.getPlayCount());
            contentValues.put(Contants.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            contentValues.put(SUB_ALBUM_ID, album.getId());

            db.insert(Contants.SUB_TB_NAME, null, contentValues);
            db.setTransactionSuccessful();
            isAlbum = true;
        } catch (Exception e) {
            isAlbum = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();

            }

            if (mCallBack != null) {
                mCallBack.onAddResult(isAlbum);
            }
        }
    }

    @Override
    public void deAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isdeAlbum = false;
        try {
            db = mXimalayaDbHelper.getWritableDatabase();
            db.beginTransaction();
            //int delete = db.delete(Contants.SUB_TB_NAME, SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            int delete = db.delete(Contants.SUB_TB_NAME, Contants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.e(TAB, "delete =" + delete + " 条");
            db.setTransactionSuccessful();
            isdeAlbum = true;
        } catch (Exception e) {
            isdeAlbum = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();

            }
            if (mCallBack != null) {
                mCallBack.onDeleteResult(isdeAlbum);
            }

        }
    }

    @Override
    public void getListAlbum() {
        SQLiteDatabase db = null;
        List<Album> albumList = new ArrayList<>();
        try {
            db = mXimalayaDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(SUB_TB_NAME, null, null, null,
                    null, null, "_id desc");
            while (query.moveToNext()) {
                Album album = new Album();
                //封面图片
                String coverUrl = query.getString(query.getColumnIndex(Contants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                //
                String title = query.getString(query.getColumnIndex(Contants.SUB_TITLE));
                album.setAlbumTitle(title);
                //
                String description = query.getString(query.getColumnIndex(Contants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //
                int tracksCount = query.getInt(query.getColumnIndex(Contants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);
                //
                int playCount = query.getInt(query.getColumnIndex(Contants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);
                //
                int albumId = query.getInt(query.getColumnIndex(Contants.SUB_ALBUM_ID));
                album.setId(albumId);
                String authorName = query.getString(query.getColumnIndex(Contants.SUB_AUTHOR_NAME));

                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);

                albumList.add(album);
            }

            query.close();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();

            }
            if (mCallBack != null) {
                mCallBack.OnSubListLoaded(albumList);
            }
        }
    }
}

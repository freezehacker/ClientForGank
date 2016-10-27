package com.sysu.sjk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sysu.sjk.bean.Gank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by sjk on 16-10-24.
 */
public class GankDao {

    DBOpenHelper helper;
    SQLiteDatabase db;

    public GankDao(Context context) {
        helper = new DBOpenHelper(context);
    }

    public void saveGank(Gank gank) {
        db = helper.getReadableDatabase();
        String sql = "INSERT INTO " + DBOpenHelper.TABLE_GANK + " VALUES(?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new String[] {
                gank.get_id(),
                gank.getCreatedAt(),
                gank.getDesc(),
                gank.getPublishedAt(),
                gank.getSource(),
                gank.getType(),
                gank.getUrl(),
                (gank.isUsed() ? "T" : "F"),
                gank.getWho(),
                gank.getContent()
        });
        db.close();
    }

    public void saveGankList(List<Gank> gankList) {
        db = helper.getReadableDatabase();
        String sql = "INSERT INTO " + DBOpenHelper.TABLE_GANK + " VALUES(?,?,?,?,?,?,?)";
        Iterator<Gank> iterator = gankList.iterator();
        while (iterator.hasNext()) {
            Gank gank = iterator.next();
            db.execSQL(sql, new String[] {
                    gank.get_id(),
                    gank.getCreatedAt(),
                    gank.getDesc(),
                    gank.getPublishedAt(),
                    gank.getSource(),
                    gank.getType(),
                    gank.getUrl(),
                    (gank.isUsed() ? "T" : "F"),
                    gank.getContent()
            });
        }
        db.close();
    }

    public Gank getGank(String id) {
        db = helper.getReadableDatabase();
        String sql = "SELECT * FROM " + DBOpenHelper.TABLE_GANK + " WHERE _id=?";
        Cursor cursor = null;
        cursor = db.rawQuery(sql, new String[] {id});
        Gank gank = null;
        if (cursor != null) {
            gank = new Gank(id);
            gank.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gank.setPublishedAt(cursor.getString(cursor.getColumnIndex("publishedAt")));
            gank.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
            gank.setSource(cursor.getString(cursor.getColumnIndex("source")));
            gank.setType(cursor.getString(cursor.getColumnIndex("type")));
            gank.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            gank.setUsed(cursor.getString(cursor.getColumnIndex("used")).equals("T"));
            gank.setWho(cursor.getString(cursor.getColumnIndex("who")));
            gank.setContent(cursor.getString(cursor.getColumnIndex("content")));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return gank;
    }

    // According to the publish date, descendant.
    public List<Gank> getAllGank() {
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBOpenHelper.TABLE_GANK, null, null,null,null,null, "publishedAt DESC");
        List<Gank> gankList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Gank gank = new Gank(cursor.getString(cursor.getColumnIndex("_id")));
                gank.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
                gank.setPublishedAt(cursor.getString(cursor.getColumnIndex("publishedAt")));
                gank.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                gank.setSource(cursor.getString(cursor.getColumnIndex("source")));
                gank.setType(cursor.getString(cursor.getColumnIndex("type")));
                gank.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                gank.setUsed(cursor.getString(cursor.getColumnIndex("used")).equals("T"));
                gank.setWho(cursor.getString(cursor.getColumnIndex("who")));
                gank.setContent(cursor.getString(cursor.getColumnIndex("content")));

                gankList.add(gank);
            }
        }
        db.close();
        return gankList;
    }

    public void deleteGank(String id) {
        db = helper.getReadableDatabase();
        String sql = "DELETE FROM " + DBOpenHelper.TABLE_GANK + " WHERE _id=" + id;
        db.execSQL(sql);
        db.close();
    }

    public void deleteAllGanks() {
        db = helper.getReadableDatabase();
        String sql = "DELETE FROM " + DBOpenHelper.TABLE_GANK;
        db.execSQL(sql);
        db.close();
    }

    public void updateGank(Gank newGank) {
        db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("_id", newGank.get_id());
        cv.put("who", newGank.getWho());
        cv.put("desc", newGank.getDesc());
        cv.put("url", newGank.getUrl());
        cv.put("content", newGank.getContent());
        cv.put("createdAt", newGank.getCreatedAt());
        cv.put("publishedAt", newGank.getPublishedAt());
        cv.put("source", newGank.getSource());
        cv.put("type", newGank.getType());
        db.update(DBOpenHelper.TABLE_GANK, cv, "_id=?", new String[]{newGank.get_id()});
        db.close();
    }

    // helper class
    public class DBOpenHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "gank01.db";
        private static final int VERSION_NUMBER = 1;
        public static final String TABLE_GANK = "gank_table_v0";
        private String createSql =
                "CREATE TABLE %s ("
                        + "_id VARCHAR(30) PRIMARY KEY,"
                        + "createdAt VARCHAR(30),"
                        + "desc VARCHAR(255),"
                        + "publishedAt VARCHAR(30),"
                        + "source VARCHAR(20),"
                        + "type VARCHAR(15),"
                        + "url VARCHAR(255),"
                        + "used CHARACTER(1),"
                        + "who VARCHAR(50),"
                        + "content BLOB"
                        + ");";

        // Common-used constructor.
        public DBOpenHelper(Context context) {
            this(context, DB_NAME, null, VERSION_NUMBER);
        }

        // To change the version, upgrade the DB.
        public DBOpenHelper(Context context, String dbName, int version) {
            this(context, dbName, null, version);
        }

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sql = String.format(createSql, TABLE_GANK);
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_GANK);
            onCreate(sqLiteDatabase);
        }
    }
}

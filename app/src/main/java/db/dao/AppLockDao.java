package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import db.AppLockOpenHelper;


/**
 * Created by anxi on 16-12-6.
 */

public class AppLockDao {

    private final AppLockOpenHelper appLockOpenHelper;

    private AppLockDao(Context context) {
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    //声明一个当前类
    private static AppLockDao appLockDao = null;

    public static AppLockDao getInstance(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    public void insert(String packagename) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packagename);
        db.insert("applock", null, contentValues);
        db.close();
    }

    public void delete(String packagename) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        db.delete("applock", "packagename = ?", new String[]{packagename});
        db.close();
    }

    public List<String> findAll() {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> lockAppList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            lockAppList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lockAppList;
    }


}

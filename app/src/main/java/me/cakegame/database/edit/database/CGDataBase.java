package me.cakegame.database.edit.database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CGDataBase extends SQLiteOpenHelper {
    public static CGDataBase CGDB = null;

    public static boolean openDataBase(Context context, String file){
        CGDB = new CGDataBase(context, file);
        boolean isOpen = CGDB.getReadableDatabase().isOpen();
        if (isOpen == false)
        {
            CGDB = null;
        }
        return isOpen;
    }

    private CGDataBase(Context context, String file) {
        super(context, file, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static List<String> getAllUserID() {
        if (CGDataBase.CGDB == null) return null;
        List<String> ids = new ArrayList<>();
        try {
            Cursor cursor = CGDataBase.CGDB.getReadableDatabase().rawQuery("SELECT distinct ID FROM Basic_User",null);
            while (cursor.moveToNext())
            {
                ids.add(cursor.getString(0));
            }
            cursor.close();
        }catch (SQLiteException e)
        {
            Log.d("SQLiteException", e.toString());
        }

        return ids;
    }

    public static List<String> getUserAttr(String userid) {
        if (CGDataBase.CGDB == null) return null;

        List<String> attr = new ArrayList<>();
        try {

            Cursor cursor = CGDataBase.CGDB.getReadableDatabase().rawQuery("SELECT Node,Item,Data FROM Basic_User WHERE ID = ?", new String[]{userid});
            while (cursor.moveToNext()) {
                attr.add(cursor.getString(0));
                attr.add(cursor.getString(1));
                attr.add(cursor.getString(2));
            }
            cursor.close();
        }catch (SQLiteException e)
        {
            Log.d("SQLiteException", e.toString());
        }
        return attr;
    }

    public static void updateUserAttr(String userid,String node,String item,String data) {
        CGDataBase.CGDB.getWritableDatabase().execSQL("UPDATE Basic_User SET data=? WHERE ID=? AND Node=? AND Item=?", new String[]{data,userid,node,item});
    }

    public static String attrTranslation(String name) {
        switch (name)
        {
            case "Basic":
                return "基础";
            case "LV":
                return "等级";
            case "HP":
                return "生命";
            case "MP":
                return "魔法";
            case "AD":
                return "物攻";
            case "AP":
                return "魔攻";
            case "P_HP":
                return "剩余生命";
            case "P_MP":
                return "剩余魔法";
            case "Defense":
                return "防御";
            case "Hit":
                return "命中";
            case "Dodge":
                return "闪避";
            case "AbsorbHP":
                return "吸血";
            case "Name":
                return "昵称";
            case "Location":
                return "位置";
            case "Occupation":
                return "职业";
            case "Task":
                return "任务";
            case "Crit":
                return "暴击";
            case "MyUnion":
                return "公会";
            case "Currency_gold":
                return "金币";
            case "Currency_diamond":
                return "钻石";
            case "CurrentExp":
                return "当前经验";
            case "NeedExp":
                return "需求经验";

        }
        return name;
    }

    public static void close_database(Context context){
        if (CGDB != null)
        {
            CGDB.close();
            CGDB = null;
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("database.close"));
        }
    }
}

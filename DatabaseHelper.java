package com.websarba.wings.android.databasesample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //データベースファイル名の定数フィールド
    private static final String DATABASE_NAME = "cocktailmemos.db";

    //バージョン情報の定数フィールド
    private static final int  DATABASE_VERSION = 1;

    //コンストラクタ
//    第1引数：コンテキスト
//    第2引数：使用するDB名
//    第3引数：通常はNull
//    第4引数：DBのバージョン番号。通常は１
    public DatabaseHelper(Context context){
        //親クラスのコンストラクタの呼び出し
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //デーブルを作るメソッド
    @Override
    public void onCreate(SQLiteDatabase db){
        //テーブル作成用SQL文字列の作成
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE cocktailmemos(");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("name TEXT,");
        sb.append("note TEXT");
        sb.append(");");
        String sql = sb.toString();

        //SQLの実行
        db.execSQL(sql);
    }

    //アップグレードするメソッド
//    第1引数：データベース接続オブジェクト
//    第2引数：内部DBの現在のバージョン番号
//    第3引数：コンストラクタで設定されたバージョン番号
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}

package com.websarba.wings.android.databasesample;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //選択されたカクテルの主キーIDを表すフィールド
    private int _cocktailId = -1;

    //選択されたカクテル名を表すフィールド
    private String _cocktailName = "";

    //データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //カクテルリスト用ListView(lvCocktail)を取得
        ListView lvCocktail = findViewById(R.id.lvCocktail);

        //lvCocktailにリスナを登録
        lvCocktail.setOnItemClickListener(new ListItemClickListener());

        //DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(MainActivity.this);
    }

//    アクティビティを終了する時に解放処理が必要になる
    @Override
    protected void onDestroy(){
        //DBヘルパーオブジェクトの解放
        _helper.close();
        super.onDestroy();
    }

    //保存ボタンがタップされたときの処理メソッド
    public void onSaveButtonClick(View view){

        //入力された感想欄を取得
        EditText etNote = findViewById(R.id.etNote);
        String note = etNote.getText().toString();

        //１．データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();

//        ---------------------------削除SOL----------------------------------------------------------

        //まず、リストで選択されたカクテルのメモデータを削除。その後インサートを行う
        //２．削除用SQL文字列を用意
        String sqlDelete = "DELETE FROM cocktailmemos WHERE _id = ?";

        //３．SQL文字列を元にプリペアドステートメントを取得
//        引数は２．で作成したSQL文を使う
        SQLiteStatement stmt = db.compileStatement(sqlDelete);

        //変数のバインド
//        バインドをすることで、「？」に変数を埋め込む
        stmt.bindLong(1, _cocktailId);

        //削除SQLの実行
        stmt.executeUpdateDelete();

//        ---------------------------削除SOL----------------------------------------------------------

//        ---------------------------insertSQL----------------------------------------------------------------------

        //２．インサート用SQL文字列の用意
        String sqlInsert = "INSERT INTO cocktailmemos (_id, name, note) VALUES (?, ?, ?)";

        //３．SQL文字列を元にプリペア度ステートメントを取得
//        引数は２．で作成したSQL文を使う
        stmt = db.compileStatement(sqlInsert);

        //変数のバインド
//        バインドをすることで、「？」に変数を埋め込む
        stmt.bindLong(1, _cocktailId);
        stmt.bindString(2, _cocktailName);
        stmt.bindString(3, note);

        //インサートSQLの実行
        stmt.executeInsert();
//        ---------------------------insertSQL----------------------------------------------------------------------


        //感想欄の入力値を消去
        etNote.setText("");

        //カクテル名を「未選択」に変更
        TextView tvCocktailName = findViewById(R.id.tvCocktailName);
        tvCocktailName.setText(getString(R.string.tv_name));

        //[保存]ボタンをタップできないように変更
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setEnabled(false);
    }

    //リストがタップされたときの処理が記述されたメンバクラス
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            //タップされた行番号をフィールドの主キーIDに代入
            _cocktailId = position;

            //タップされた行のデータを取得。これがカクテル名となるので、フィールドに代入
            _cocktailName = (String) parent.getItemAtPosition(position);

            //カクテル名を表示するTextViewに表示カクテル名を設定
            TextView tvCocktailName = findViewById(R.id.tvCocktailName);
            tvCocktailName.setText(_cocktailName);

            //[保存]ボタンがタップできるように設定
            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setEnabled(true);

//        ---------------------------selectSQL----------------------------------------------------------------------


            //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();

            //主キーによる検索SQL文字列の用意
            String sql = "SELECT * FROM cocktailmemos WHERE _id = " + _cocktailId;

            //SQLの実行
            //rawQuery([SQL文字列],[バンド変数用String配列（今回はnull）])
//            第2引数はSELECTのSQL文に「？」が入っていた場合に、「？」に入れる変数を入れる
//            String以外の場合は、Stirngに変換してから入れる
            Cursor cursor = db.rawQuery(sql, null);

            //データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";

            //SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while (cursor.moveToNext()){//MoveToNext()で次の行に飛ぶ
                //カラムのインデックス値を取得
                int idxNote = cursor.getColumnIndex("note");

                //カラムのインデックス値をもとに実際のデータを取得
                note = cursor.getString(idxNote);
            }

//        ---------------------------selectSQL----------------------------------------------------------------------

            //感想のEditTextの各画面部分を取得しデータベースの値を反映
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText(note);
        }
    }
}
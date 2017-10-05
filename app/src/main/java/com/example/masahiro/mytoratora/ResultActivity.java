package com.example.masahiro.mytoratora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.masahiro.mytoratora.R.id.result_label;

public class ResultActivity extends AppCompatActivity {
    final int JANKEN_GU = 0;
    final int JANKEN_CHOKI = 1;
    final int JANKEN_PA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int myHand = 0;

        //データを取り出す
        Intent intent = getIntent();
        int id = intent.getIntExtra("MY_HAND", 0);

        ImageView myHandImageView = (ImageView) findViewById(R.id.my_hand_image);
        switch (id) {
            case R.id.gu:
                myHandImageView.setImageResource(R.drawable.tiger);//送られてきた画像をsetする
                myHand = JANKEN_GU;//プレイヤーの手を保持する
                break;
            case R.id.choki:
                myHandImageView.setImageResource(R.drawable.reewoman);//送られてきた画像をsetする
                myHand = JANKEN_CHOKI;//プレイヤーの手を保持する
                break;
            case R.id.pa:
                myHandImageView.setImageResource(R.drawable.kiyom);//送られてきた画像をsetする
                myHand = JANKEN_PA;//プレイヤーの手を保持する
                break;
            default:
                break;
        }

        //comの手を決める
        int comHand = (int) (Math.random() * 3);
        comHand = getHand();
        ImageView comHandImageView = (ImageView) findViewById(R.id.com_hand_image);
        switch (comHand) {
            case JANKEN_GU:
                comHandImageView.setImageResource(R.drawable.tiger);
                break;
            case JANKEN_CHOKI:
                comHandImageView.setImageResource(R.drawable.reewoman);
                break;
            case JANKEN_PA:
                comHandImageView.setImageResource(R.drawable.kiyom);
                break;
        }

        //勝敗の判定
        TextView resultLabel = (TextView) findViewById(result_label);
        int gameResult = (comHand - myHand + 3) % 3;
        switch (gameResult) {
            case 0:
                //あいこ
                resultLabel.setText(R.string.result_draw);//メッセージを出す
                break;
            case 1:
                //かち
                resultLabel.setText(R.string.result_win);//メッセージを出す
                break;
            case 2:
                //負け
                resultLabel.setText(R.string.result_lose);//メッセージを出す
        }
        saveData(myHand, comHand, gameResult);
    }

    //戻るボタンのメソッド
    public void onBackButtonTapped(View view) {
        finish();
    }

    //勝敗情報のセーブ
    public void saveData(int myHand, int comHand, int gameResult) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        int gameCount = pref.getInt("GAME_COUNT", 0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0);
        int lastComHand = pref.getInt("LAST_COM_HAND", 0);
        int lastGameResult = pref.getInt("GAME_RESULT", -1);
        editor.putInt("GAME_COUNT", gameCount + 1);
        if (lastGameResult == 2 && gameResult == 2) {
            //コンピュータが連勝した場合
            editor.putInt("WINNIG_STREAK_COUNT", winningStreakCount + 1);
        } else {
            editor.putInt("WINNING_STREAK_COUNT", 0);
        }
        editor.putInt("LAST_MY_HAND", myHand);
        editor.putInt("LAST_COM_HAND", comHand);
        editor.putInt("BEFORE_LAST_COM_HAND", lastComHand);
        editor.putInt("GAME_RESULT", gameResult);

        editor.commit();
    }

    //心理学に基づいたじゃんけんのロジック
    private int getHand() {
        int hand = (int) (Math.random() * 3);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int gameCount = pref.getInt("GAME_COUNT", 0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0);
        int lastmyHand = pref.getInt("LAST_MY_HAND", 0);
        int lastComHand = pref.getInt("LAST_COM_HAND", 0);
        int beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0);
        int gameResult = pref.getInt("GAME_RESULT", -1);

        if (gameCount == 1) {
            if (gameResult == 2)
                //前回の勝負で1回目で、コンピュータが勝った場合,コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
        } else if (gameResult == 1) {
            //前回の勝負が一回目で、コンピュータが負けた場合相手の出した手に勝つ
            hand = (lastmyHand - 1 + 3) % 3;
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                //同じ手は連勝した場合は手を変える
                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
            }
        }
        return hand;
    }
}

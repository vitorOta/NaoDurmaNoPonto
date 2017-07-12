package br.com.vitorota.naodurmanoponto.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import br.com.vitorota.naodurmanoponto.MyApp;

/**
 * Created by Vitor Ota on 29/06/2016.
 */
public class PiscarActivity extends Activity {

    private LinearLayout ll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        setContentView(ll);

        boolean piscar = MyApp.getConfig(MyApp.Configs.CONFIG_PISCAR, false);


        if (piscar) {
            new PiscarTask().execute();
        }
//        Animation anim = new AlphaAnimation(0f,1f);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(250);
//        anim.setStartOffset(20);
//        ll.startAnimation(anim);
        //não funcionou, pesquisar melhor depois
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("PARAR",true);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    private class PiscarTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            boolean b = true;
            while (true) {
                int color = Color.BLACK;
                if (b) {
                    color = Color.WHITE;
                }
                publishProgress(color);
                b = !b;
                try {
                    Thread.sleep(575);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            ll.setBackgroundColor(values[0]);
        }
    }

}

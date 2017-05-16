package com.leon.text.findandtext;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by LeonWork on 2/18/2017.
 */

public class SplashScreen extends AppCompatActivity {//implements View.OnClickListener{

    //private final long SPLASH_DISPLAY_LENGTH = 999999999;
   // public static String prefName = "Random";
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private Button ok;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        ok = (Button) findViewById(R.id.btn_ok_splash);
        //ok.setOnClickListener(this);
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
        edit = sp.edit();
        edit.putBoolean("firstTime", false);
        edit.commit();
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashScreen.this,Menu.class);
//                SplashScreen.this.startActivity(mainIntent);
//                SplashScreen.this.finish();
//            }
//        }, SPLASH_DISPLAY_LENGTH);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
                edit = sp.edit();
                edit.putBoolean("firstTime", true);
                edit.commit();
//                Intent mainIntent = new Intent(SplashScreen.this,LandingScreen.class);
//                startActivity(mainIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
//        Toast.makeText(this, R.string.acknowledge, Toast.LENGTH_SHORT).show();
    }
}


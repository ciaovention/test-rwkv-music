package com.example.myapplication;
import com.rwkv.faster.*;
import android.os.Environment;
import android.content.Intent;
import android.provider.Settings;
import java.util.Arrays;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    static {
        System.loadLibrary("faster_rwkv_jni");
    }
    private static int[] add2BeginningOfArray(int[] elements, int element)
    {
        int[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }


    public String generateResult(String input, Model model, ABCTokenizer tokenizer, Sampler sampler, float temperature, int top_k, float top_p, int BOS_ID) {
        int[] input_ids = tokenizer.encode(input);
        input_ids = add2BeginningOfArray(input_ids, BOS_ID);

        float[] logits = model.run(input_ids);

        StringBuilder result = new StringBuilder(); // Use StringBuilder for better performance
        for (int i = 0; i < 300; i++) {
            int output_id = sampler.sample(logits, temperature, top_k, top_p);
            String output = tokenizer.decode(output_id);
            // Log.i("output", output);
            result.append(output);
            logits = model.run(output_id);
        }

        return result.toString();
    }
    private void hideSystemUI() {
        // Enables sticky immersive mode.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    private Model model;
    private ABCTokenizer tokenizer;
    private Sampler sampler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();//全屏显示
        TextView resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setText("Test");
        String all_input = "";
        String input = "D | G2 G G2 B | ABA A2 B | G2 G G2 B | d3 g2 D | GAG G2 B | ABA A2 f | g2 e edB | d3 d2 :: d | \n" +
                "     gag fed | efe edB | d2 d d2 e | g3 g2 a | b2 g g2 d | efe d2 B | g2 G G2 B | d3 d2 :|S:2";

        input ="A2 | d2 d>d d2 f>e | d2 A2 d2 e2 | f2 f>f f2 a>g | f2 e2 f2 g2 | a2 a>a a2 b>a | g2 g>g g2 a>g | \n" +
                "     f2 ed a2 gf | e2 e>e e2 :: A2 | A>BA>B c>dc>d | e>fe>f g2 fe | d>ed>e f>gf>g | a>ba>b =c'2 ba | \n" +
                "     bgeb afda | g>fe>d c2 B>A |";
        //String result = input;
        Log.i("xxx", input);
        boolean k = true;
        if (k == true) {
            model = new Model("asset:RWKV-5-ABC-82M-v1-20230901-ctx1024-ncnn", "ncnn fp32", getAssets());
            tokenizer = new ABCTokenizer();
            sampler = new Sampler();
            Log.i("xxx", "model loaded!!!!!!");

            int BOS_ID = 2;
            int EOS_ID = 3;
            float temperature = 1.0f;  // Adjusted temperature to a more typical value
            int top_k = 1;
            float top_p = 0.9f;
            for (int i = 0; i < 20; i++) { // Adjust the number of iterations as needed
                input = generateResult(input, model, tokenizer, sampler, temperature, top_k, top_p, BOS_ID);
                Log.i("written", input);
                all_input += input;
                resultTextView = findViewById(R.id.resultTextView);
                resultTextView.setText(all_input);
            }
        }
        ///////////////////


    }

}
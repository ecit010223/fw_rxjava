package com.huier.fw_rxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RxJava5Activity extends AppCompatActivity {

    public static void entry(Context from){
        Intent intent = new Intent(from,RxJava5Activity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java5);
    }
}

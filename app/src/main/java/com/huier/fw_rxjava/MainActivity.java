package com.huier.fw_rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRxJava1;
    private Button btnRxJava4;
    private Button btnRxJava5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRxJava1 = (Button)findViewById(R.id.btn_rxjava1);
        btnRxJava1.setOnClickListener(this);
        btnRxJava4 = (Button)findViewById(R.id.btn_rxjava4);
        btnRxJava4.setOnClickListener(this);
        btnRxJava5 = (Button)findViewById(R.id.btn_rxjava5);
        btnRxJava5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_rxjava1:
                RxJava1Activity.entry(this);
                break;
            case R.id.btn_rxjava4:
                RxJava4Activity.entry(this);
                break;
            case R.id.btn_rxjava5:
                RxJava5Activity.entry(this);
                break;
        }
    }
}

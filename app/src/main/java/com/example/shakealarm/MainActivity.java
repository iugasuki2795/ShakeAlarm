package com.example.shakealarm;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Switch swt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swt = (Switch)findViewById(R.id.onoff);
        swt.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton cb , boolean isChecking )
            {
                String str = String.valueOf(isChecking);

                if(isChecking)
                    Toast.makeText(getApplication(),"알림켜기 ",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplication(),"알림끄기 ",Toast.LENGTH_SHORT).show();

            }

        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "닉네임 바꾸기", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}

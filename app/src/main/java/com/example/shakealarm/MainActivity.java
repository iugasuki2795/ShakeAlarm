package com.example.shakealarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Switch swt;
    private ListView listView;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> list = getIntent().getStringArrayListExtra("list");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        if(PreferencesManager.isEnabled(this)){
            Intent intent = new Intent(this, AppService.class);
            startService(intent);
        }
        swt = (Switch)findViewById(R.id.onoff);
        swt.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton cb , boolean isChecking )
            {
                String str = String.valueOf(isChecking);
                PreferencesManager.setEnabled(getBaseContext(), isChecking);
                if(isChecking){
                    Toast.makeText(getApplication(),"알림켜기 ",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), AppService.class);
                    startService(intent);
                }
                else {
                    Toast.makeText(getApplication(), "알림끄기 ", Toast.LENGTH_SHORT).show();
                }

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

        editText = (EditText)findViewById(R.id.edit_text);
        editText.setText("코드이름");
    }

}

package com.example.shakealarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Switch swt;
    private ListView listView;
    private EditText editText;
    private Button button;
    TextView dTextView = null;
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
        swt.setChecked(PreferencesManager.isEnabled(this));
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
                //Snackbar.make(view, "설정", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                //수정 시작한 부분
                dTextView  = new TextView(getBaseContext());
                dTextView.setText("탈퇴하시겠습니까?");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getBaseContext(), AppService.class);
                        stopService(intent);
                        TheThread thread = new TheThread(TheThread.MODE_DELETE, getBaseContext());
                        thread.start();
                    }
                });
                builder.setNegativeButton("취소", null);

                builder.setView(dTextView);
                AlertDialog worklistDialog= builder.create();
                worklistDialog.show();


            }
        });
       //밑에 두 개는 리스너 달아 놓기만 하면 됨
       //카카오톡 앱 켜는 버튼
       FloatingActionButton kakao = (FloatingActionButton)findViewById(R.id.fab_kakao);
       kakao.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Snackbar.make(view, "카카오톡 연결", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show();

           }
       });
       //음성녹음 버튼
       FloatingActionButton rec =(FloatingActionButton)findViewById(R.id.fab_rec);
       rec.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Snackbar.make(view, "음성녹음", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show();

           }
       });
        editText = (EditText)findViewById(R.id.edit_text);
        String str = PreferencesManager.getRoomName(this);
        editText.setText(str);

        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TheThread theThread = new TheThread(TheThread.MODE_UPDATE,getBaseContext(),editText.getText().toString());
                theThread.start();
            }
        });

    }

}

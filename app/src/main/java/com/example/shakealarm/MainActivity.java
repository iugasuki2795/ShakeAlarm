package com.example.shakealarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ToggleButton;

import java.util.ArrayList;

import static com.example.shakealarm.TheThread.mode.DELETE;
import static com.example.shakealarm.TheThread.mode.FILE;
import static com.example.shakealarm.TheThread.mode.UPDATE;

public class MainActivity extends AppCompatActivity {

    VoiceRecorder vr;

    private ToggleButton swt;
    private ListView listView;
    private EditText editText;
    private Button button;
    TextView dTextView = null;
    FloatingActionButton delete;//삭제 버튼

    FloatingActionButton plus; //fab와 rec를 띄우기 위한 버튼
    FloatingActionButton rec;//음성녹음 버튼
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vr = new VoiceRecorder(this);
        ArrayList<String> list = getIntent().getStringArrayListExtra("list");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        if(PreferencesManager.isEnabled(this)){
            Intent intent = new Intent(this, AppService.class);
            startService(intent);
        }
        swt = (ToggleButton)findViewById(R.id.onoff);
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

        delete = (FloatingActionButton) findViewById(R.id.fab);
        delete.setOnClickListener(new View.OnClickListener() {
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
                        TheThread thread = new TheThread(DELETE, getBaseContext());
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
       plus= (FloatingActionButton)findViewById(R.id.plus);
       plus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               ToggleFab();

           }
       });
       //음성녹음 버튼
       rec =(FloatingActionButton)findViewById(R.id.fab_rec);
       rec.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                if(!vr.isRecording()){
                    vr.onRecord(true);
                }else if(vr.isRecording()){
                    vr.onRecord(false);
                    vr.onPlay(true);
                    TheThread thread = new TheThread(FILE, getBaseContext(), vr);
                    thread.start();
                }

           }
       });
        editText = (EditText)findViewById(R.id.edit_text);
        String str = PreferencesManager.getRoomName(this);
        editText.setText(str);

        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TheThread theThread = new TheThread(UPDATE,getBaseContext(),editText.getText().toString());
                theThread.start();
            }
        });

    }

    private void ToggleFab() {
        // 버튼들이 보여지고있는 상태인 경우 숨겨줍니다.
        if(delete.getVisibility() == View.VISIBLE) {
            delete.hide();
            rec.hide();
            delete.animate().translationY(0);
            rec.animate().translationY(0);
        }
        // 버튼들이 숨겨져있는 상태인 경우 위로 올라오면서 보여줍니다.
        else {
            // 중심이 되는 버튼의 높이 + 마진 만큼 거리를 계산합니다.
            int dy = plus.getHeight() + 20;
            delete.show();
            rec.show();
            // 계산된 거리만큼 이동하는 애니메이션을 입력합니다.
            delete.animate().translationY(-dy*2);
            rec.animate().translationY(-dy);
        }
    }




}

package com.example.dico.last;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_IP = "ip";
    ListView listView;
    DatabaseAction dbAction;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> hashMap;
    SimpleAdapter sAdapter;
    private TextView textView;
    private EditText editText;
    private ImageView imageView;

    public static boolean checkWithRegExp(String userNameString) {
        Pattern p = Pattern.compile("^(22[0]|2[0-2][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\\.(22[0]|2[0-2][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$");
        Matcher m = p.matcher(userNameString);
        return m.matches();
    }

    public static boolean localIp(String userNameString) {
        Pattern p = Pattern.compile("^(10\\.\\d+|172\\.(1[6-9]|2\\d|3[0-1])|192\\.168)(\\.\\d+){2}$");
        Matcher m = p.matcher(userNameString);
        return m.matches();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int ip = 0;
        int country = 1;
        int city = 2;
        int flag = 3;

        dbAction = new DatabaseAction(this);
        data = new ArrayList<Map<String, Object>>();

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);

        String[] from = {ATTRIBUTE_NAME_IP, ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE};
        int[] to = {R.id.tvIp, R.id.tvText, R.id.ivImg};
        sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);
        listView.setAdapter(sAdapter);

        dbAction.open();

        if (isNetworkAvailable(this)) {
            Task task = new Task(this);
            task.execute();

            String finishResult = null;
            try {
                finishResult = task.get();

                String[] stringResult;
                stringResult = finishResult.split("\n");
                textView.setText(stringResult[ip] + ", " + stringResult[country] + ", " + stringResult[city]);
                int resID = getResources().getIdentifier(stringResult[flag].toLowerCase(), "drawable", getPackageName());
                imageView.setImageResource(resID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(this, CheckInternet.class);
            startActivity(intent);

        }
    }

    public void clickButtonGetInfo(View v) throws ExecutionException, InterruptedException {
        data.clear();
        sAdapter.notifyDataSetChanged();
        int etLine = 0;

        String etText = editText.getText().toString();
        String[] etAllIp = etText.split("\n");

        while (etLine < etAllIp.length) {
            if (checkWithRegExp(etAllIp[etLine])) {
                if (!localIp(etAllIp[etLine])) {
                    int numberRepeats = 0;

                    for (int i = 0; i < etAllIp.length; i++) {
                        if (etAllIp[etLine].equals(etAllIp[i])) {
                            numberRepeats++;
                        }
                    }

                    Task task = new Task(this);
                    String etIp = etAllIp[etLine] + "\n" + numberRepeats;
                    task.execute(etIp);
                    etLine++;
                } else {
                    Toast.makeText(this, "Приватный ip", Toast.LENGTH_SHORT).show();
                    break;
                }
            } else {
                Toast.makeText(this, "Не верный ip", Toast.LENGTH_SHORT).show();
                etLine++;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAction.close();
    }
}

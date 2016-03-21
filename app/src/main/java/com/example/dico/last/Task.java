package com.example.dico.last;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created BY Dico on 16.03.16.
 */
public class Task extends AsyncTask<String, String, String> {
    MainActivity ma;
    private URL url;
    private int ip = 0;
    private int country = 1;
    private int city = 2;

    public Task(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String... params) {

        Cursor cursorAllDb = ma.dbAction.getAllData();
        String[] strAllDb = new String[cursorAllDb.getCount()];
        int line = 0;
        String result = null;

        if (cursorAllDb.moveToFirst()) {
            do {
                strAllDb[line] = cursorAllDb.getString(cursorAllDb.getColumnIndex("ip"));
                line++;
            } while (cursorAllDb.moveToNext());
        }
        cursorAllDb.close();

        if (params.length != 0) {
            String[] strIP = params[0].split("\n");
            Boolean bool = true;

            for (int i = 0; i < strAllDb.length; i++) {
                if (strIP[ip].equals(strAllDb[i])) {
                    Cursor ipCursor = ma.dbAction.getOldIp(strIP[ip]);
                    ipCursor.moveToFirst();
                    String ipStr = ipCursor.getString(ipCursor.getColumnIndex("ip"));
                    String countryStr = ipCursor.getString(ipCursor.getColumnIndex("country"));
                    String cityStr = ipCursor.getString(ipCursor.getColumnIndex("city"));
                    int flag = ipCursor.getInt(ipCursor.getColumnIndex("flag"));

                    result = ipStr + "\n" + countryStr + "\n" + cityStr + "\n" + flag + "\n" + strIP[1];
                    ipCursor.close();
                    bool = false;
                    break;
                }
            }

            if (bool == true) {
                result = parseJson(getJson(strIP[ip]));
                String[] finish = result.split("\n");
                int resID = ma.getResources().getIdentifier(finish[3].toLowerCase(), "drawable", ma.getPackageName());

                result = finish[ip] + "\n" + finish[country] + "\n" + finish[city] + "\n" + resID + "\n" + strIP[1];
            } else {

            }
        } else {
            result = parseJson(getJson(null));
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected String parseJson(String strJson) {
        JSONObject dataJsonObj;
        String result = "";
        try {
            dataJsonObj = new JSONObject(strJson);
            String ip = dataJsonObj.getString("ip");

            JSONObject country = dataJsonObj.getJSONObject("country");
            String countryStr = country.getString("name_en");

            JSONObject city = dataJsonObj.getJSONObject("city");
            String cityStr = city.getString("name_en");

            String iso = country.getString("iso");

            result = ip + "\n" + countryStr + "\n" + cityStr + "\n" + iso;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addToDatabase(result);
        return result;
    }

    public void addToDatabase(String result) {
        int flag;
        int line = 0;
        String[] stringResult = result.split("\n");
        flag = ma.getResources().getIdentifier(stringResult[3].toLowerCase(), "drawable", ma.getPackageName());
        Cursor allData = ma.dbAction.getAllData();
        String[] listIp = new String[allData.getCount()];
        if (allData.moveToFirst()) {
            do {
                listIp[line] = allData.getString(allData.getColumnIndex("ip"));
                line++;
            } while (allData.moveToNext());
            allData.close();
            Boolean bool = true;
            for (int j = 0; j < listIp.length; j++) {
                if (listIp[j].equals(stringResult[0])) {
                    bool = false;
                    break;
                }
            }
            if (bool == true) {
                ma.dbAction.addRec(stringResult[ip], stringResult[country], stringResult[city], flag);
            }
        } else {
            ma.dbAction.addRec(stringResult[ip], stringResult[country], stringResult[city], flag);
        }
        allData.close();

    }

    public String getJson(String ip) {
        String resultJson = "";
        HttpURLConnection urlConnection;
        BufferedReader reader;
        try {
            if (ip == null) {
                url = new URL("http://api.sypexgeo.net/");
            } else {
                url = new URL("http://api.sypexgeo.net/json/" + ip);
            }
            urlConnection = (HttpURLConnection) this.url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            Map<String, Object> item;
            int mCount = ma.listView.getCount();
            String string;
            Boolean bool = true;

            String[] finish = s.split("\n");

            if (finish.length > 4) {

                for (int i = 0; i < mCount; i++) {
                    item = ma.data.get(i);
                    string = (String) item.get(ma.ATTRIBUTE_NAME_IP);
                    if (string.equals(finish[0])) {
                        bool = false;
                        break;
                    }
                }

                if (bool == true) {
                    ma.hashMap = new HashMap<String, Object>();
                    ma.hashMap.put(ma.ATTRIBUTE_NAME_IP, finish[0]);
                    ma.hashMap.put(ma.ATTRIBUTE_NAME_TEXT, " (" + finish[4] + ")" + " - " + finish[1] + ", " + finish[2]);
                    ma.hashMap.put(ma.ATTRIBUTE_NAME_IMAGE, finish[3]);
                    ma.data.add(ma.hashMap);
                }
                ma.sAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

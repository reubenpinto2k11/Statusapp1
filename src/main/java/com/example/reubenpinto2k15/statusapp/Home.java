package com.example.reubenpinto2k15.statusapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class Home extends Activity {
    private ProgressDialog pDialog;
    JSONArray jsonArray=null;
    JSONObject jsonObject=null;
    ArrayList<HashMap<String,String>> memberList=new ArrayList<HashMap<String, String>>();
    ArrayList<ArrayList<Object>> listData=new ArrayList<ArrayList<Object>>();
    LinearLayout butnLay=null;
    LazyAdapter adapter;
    ListView listView;
    int rowSize=8;
    int page=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home);


        listView=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(this,listData);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index=(page*rowSize)+position;
                SharedPreferences sp=Home.this.getSharedPreferences("Data",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.clear();
                editor.putString("id",memberList.get(index).get("id"));
                editor.putString("dob",memberList.get(index).get("dob"));
                editor.putString("image",memberList.get(index).get("image"));
                editor.putString("status",memberList.get(index).get("status"));
                editor.putString("ethin",memberList.get(index).get("ethin"));
                editor.putString("weight",memberList.get(index).get("weight"));
                editor.putString("height",memberList.get(index).get("height"));
                editor.putString("is_veg",memberList.get(index).get("is_veg"));
                editor.putString("drink",memberList.get(index).get("drink"));
                editor.commit();
                Intent intent=new Intent(Home.this,Profile.class);
                Home.this.startActivity(intent);
            }
        });
        butnLay=(LinearLayout)findViewById(R.id.hlinearlayout);

        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            new AsyncTaskHandler().execute("http://tipstat.0x10.info/api/tipstat?type=json&query=list_status");

        }
        else
        {
            Toast.makeText(getBaseContext(),"NO NETWORK PRESENT",Toast.LENGTH_SHORT).show();
        }
    }




    private class AsyncTaskHandler extends AsyncTask<String,Void,ArrayList<HashMap<String,String>>>
    {
        InputStream inputStream=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Home.this);
            pDialog.setMessage("Loading Members. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... url)
        {
            String result="";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(new HttpGet(url[0]));
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                } else
                    result = "ERROR";

                JSONObject arryObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.getJSONArray("members");

                    result = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        arryObject = jsonArray.getJSONObject(i);
                        String id = arryObject.getString("id");
                        String dob = arryObject.getString("dob");
                        String status = arryObject.getString("status");
                        String ethin = arryObject.getString("ethnicity");
                        String weight = arryObject.getString("weight");
                        String height = arryObject.getString("height");
                        String is_veg = arryObject.getString("is_veg");
                        String drink = arryObject.getString("drink");
                        String image = arryObject.getString("image");
                        map.put("id", id);
                        map.put("image", image);
                        map.put("dob", dob);
                        map.put("status", status);
                        map.put("ethin", ethin);
                        map.put("weight", weight);
                        map.put("height", height);
                        map.put("is_veg", is_veg);
                        map.put("drink", drink);
                        memberList.add(map);
                        result = result + jsonArray.getJSONObject(i).getString("id");
                    }
                   }
                catch (JSONException j) {
                    Toast.makeText(getBaseContext(), "JSON Object Parsing Failure", Toast.LENGTH_SHORT).show();
                    Log.d("JSON Object Parsing Failure", j.getLocalizedMessage());
                }
            } catch (IOException e) {
                Toast.makeText(getBaseContext(),"Error in Http Transaction",Toast.LENGTH_SHORT).show();
                Log.d("Error in Http Transaction",e.getLocalizedMessage());
            }
           return memberList;
       }


        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> s) {
            super.onPostExecute(s);
            listData.clear();
            addItem(0);
            final int size=memberList.size()/rowSize;

            for(int i=0;i<size;i++)
            {
                final int j=i;
                final Button pager=new Button(Home.this);
                LayoutParams lp=new LayoutParams(100, LayoutParams.WRAP_CONTENT);
                lp.setMargins(2,2,2,2);
                pager.setId(i);
                pager.setText(String.valueOf(i + 1));
                if(i==0)
                    pager.setBackgroundResource(R.drawable.btn_orange_matte);
                else
                    pager.setBackgroundResource(R.drawable.btn_white_matte);


                pager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int k=0;k<size;k++)
                        {
                            Button b=(Button)findViewById(k);
                            b.setBackgroundResource(R.drawable.btn_white_matte);
                        }
                        page=v.getId();
                        v.setBackgroundResource(R.drawable.btn_orange_matte);
                        listData.clear();
                        addItem(j);
                    }
                });
                butnLay.addView(pager,lp);
                Log.d("StatusApp","Button created");
            }
        }
    }

    private void addItem(int row){
        listData.clear();
        int temp;
        row=row*rowSize;
        if((row+rowSize)>memberList.size())
            temp=rowSize-((row+rowSize)-memberList.size());
        else
            temp=rowSize;
        for(int i=0;i<temp;i++)
        {
            ArrayList<Object> dat=new ArrayList<Object>();
            HashMap<String, String> map = new HashMap<String, String>();
            map=memberList.get(row);
            dat.add(i);
            dat.add(map.get("image"));
            dat.add(map.get("status"));
            listData.add(dat);
            row++;
        }
        setView();
    }

    private void setView(){
        pDialog.dismiss();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setData(listData);
                listView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

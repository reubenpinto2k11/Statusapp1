package com.example.reubenpinto2k15.statusapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Profile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        String [] e_arr={"Asian","Indian","African American","Asian American","European","British","Jewish","Latino","Native American","Arabic"};

        SharedPreferences sp=Profile.this.getSharedPreferences("Data",Context.MODE_PRIVATE);
        TextView ethin=(TextView)findViewById(R.id.ethin);
        ethin.setText(e_arr[Integer.parseInt(sp.getString("ethin","0"))]);
        ImageView pic=(ImageView)findViewById(R.id.pic);
        ImageLoader il =new ImageLoader(getApplicationContext());
        il.DisplayImage(sp.getString("image",null),pic);
        TextView dob=(TextView)findViewById(R.id.dob_date);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try {
            date = sdf.parse(sp.getString("dob", "2015-01-01"));
        }
        catch (ParseException p)
        {
            Log.d("StatusApp",p.getLocalizedMessage());
        }
        sdf=new SimpleDateFormat("dd-MMM-yyyy");
        dob.setText(sdf.format(date));
        TextView weight=(TextView)findViewById(R.id.weight_data);
        float wt=Integer.parseInt(sp.getString("weight","0"))/1000;
        weight.setText(String.valueOf(wt)+"Kgs");
        TextView height=(TextView)findViewById(R.id.ht_data);
        int ft=Integer.parseInt(sp.getString("height","0"))/12;
        int inch=Integer.parseInt(sp.getString("height","0"))%12;
        String ht=ft+"' "+inch+"''";
        height.setText(ht);
        TextView veg=(TextView)findViewById(R.id.veg_data);
        if(sp.getString("is_veg","0").equals("1"))
        {
            veg.setText("Yes");
        }
        else
        {
            veg.setText("No");
        }
        TextView drink=(TextView)findViewById(R.id.drink_data);
        if(sp.getString("drink","0").equals("1"))
        {
            drink.setText("Yes");
        }
        else
        {
            drink.setText("No");
        }

        EditText et=(EditText)findViewById(R.id.status);
       et.setText(sp.getString("status","No Status"));
    }


}

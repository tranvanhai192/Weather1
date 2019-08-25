package com.example.weatherforme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Class.Define;
import com.example.Class.ThoiTiet;
import com.example.Custom.CustomAdapter;
import com.example.SQLLITE.DataHelper;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;

    LineChart lineChart;
    CombinedChart cbchart;
    ImageView imgsearch;
    EditText edtSearch;
    TextView tvName, tvCountry, tvTemp, tvStatus, tvhumidity, tvwind, tvclouds, tvday;
    TextView tvMin, tvMax, tvdate, thu1;
    ImageView imgicon;
    ListView lstweather;
    FrameLayout fragment_container;
    CustomAdapter customAdapter;
    ArrayList<ThoiTiet> mangthoitiet;
    DataHelper mydb;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        mydb = new DataHelper(getBaseContext());
        // gọi toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // sear địa điểm
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
            }
        });
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        } else {
            return false;
        }
    }
    private void checkConnection() {
        if(isOnline()){
            Toast.makeText(MainActivity.this,getString(R.string.messInternet) , Toast.LENGTH_SHORT).show();
            SearchCity();
        }
        else{
            Toast.makeText(MainActivity.this,getString(R.string.messnotInternet) , Toast.LENGTH_SHORT).show();
        }
    }
    // sự kiện click tìm địa điểm thành phố
    public void SearchCity(){
        String city = edtSearch.getText().toString();
    if (edtSearch.length() !=0){
        GetCurrentWeatherData(city);
        GetCurrent7WeatherData(city);
        RewardApi(city);
        mydb.addData(city);
        Toast.makeText(MainActivity.this,getString(R.string.mess1),Toast.LENGTH_LONG).show();

    } else {
        Toast.makeText(MainActivity.this, getString(R.string.mess), Toast.LENGTH_LONG).show();
    }
}
    // đọc đường dẫn API weather địa điểm của tỉnh thành
    public void GetCurrentWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = Define.urlname + data + Define.appidtemp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //lấy giá trị date and
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            tvName.setText(getString(R.string.city) + name);
                            // gán và đổi giá trị datetime
                            long l = Long.valueOf(day);
                            Date date = new Date(l * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" EEEE dd-MM-yyy \n \t\t       HH:mm");
                            String Day = simpleDateFormat.format(date);
                            tvday.setText(Day);
                            // gọi đối tượng weather
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("main");// trạng thái thời tiết
                            String icon = jsonObjectWeather.getString("icon");//icon thời tiết
                            // thư viện load ảnh Glide
                            Glide.with(MainActivity.this)
                                    .load(Define.picture + icon + ".png").apply(new RequestOptions().override(250, 190).fitCenter()).into(imgicon);
                            tvStatus.setText(status);
                            //gọi đối tượng main
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String nhietdo = jsonObjectMain.getString("temp");
                            String doam = jsonObjectMain.getString("humidity");
                            Double a = Double.valueOf(nhietdo);
                            String NhietDo = String.valueOf(a.intValue());
                            tvTemp.setText(nhietdo + "°C");
                            tvhumidity.setText(doam + "%");
                            //gọi đối tượng Wind
                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String gio = jsonObjectWind.getString("speed");
                            tvwind.setText(gio + "m/s");
                            //gọi đối tượng Cloud
                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                            String may = jsonObjectCloud.getString("all");
                            tvclouds.setText(may + "%");
                            //gọi đối tượng Sys
                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String quocgia = jsonObjectSys.getString("country");

                            String sunrise = jsonObjectSys.getString("sunrise");
                            long sr = Long.valueOf(sunrise);
                            Date datesunrie = new Date(sr * 1000L);
                            SimpleDateFormat spsr = new SimpleDateFormat("HH:mm");
                            String Sunrie = spsr.format(datesunrie);

                            String sunset = jsonObjectSys.getString("sunset");
                            long ss = Long.valueOf(sunset);
                            Date datesunset = new Date(ss * 1000L);
                            SimpleDateFormat spss = new SimpleDateFormat("HH:mm");
                            String Sunset = spss.format(datesunset);

                            tvCountry.setText(getString(R.string.nations) + quocgia);
                            tvMin.setText(getString(R.string.sunrise) +"\t\t" +Sunrie);
                            tvMax.setText(getString(R.string.sunset) +"\t\t" +Sunset);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }
    // vẽ biểu đồ đường nhiệt độ
    private void RewardApi(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = Define.url7d + data + Define.appid7d;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            JSONArray jsonArraylist = jsonObject1.getJSONArray("list");
                            for (int i = 0; i < jsonArraylist.length(); i++) {
                                JSONObject jsonObjectlist = jsonArraylist.getJSONObject(i);
                                String thu = jsonObjectlist.getString("dt");
                                // gán và đổi giá trị datetime
                                long l = Long.valueOf(thu);
                                Date date = new Date(l * 1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("\t\tEEEE \n dd-MM-yyyy");
                                String Day = simpleDateFormat.format(date);

//                                for(int t=0; t < jsonArraylist.length();i++){
                                JSONObject jsonObjectTemp = jsonObjectlist.getJSONObject("temp");
                                String max=jsonObjectTemp.getString("max");
                                Double a = Double.valueOf(max);
                                String n1 = String.valueOf(a.intValue());

                                ArrayList<Entry> entries=new  ArrayList<>();
                                entries.add(new Entry(0,Float.valueOf(n1)));
                                LineDataSet dataSet = new LineDataSet(entries, "data temp");

                                dataSet.setFillAlpha(50);
                                dataSet.setColors(Color.RED);
                                dataSet.setDrawCircles(true);
                                dataSet.setDrawFilled(true);

                                LineData data1 = new LineData(dataSet);
                                lineChart.setData(data1);
                                lineChart.invalidate();

        }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);

    }
    private void GetCurrent7WeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = Define.url7d + data + Define.appid7d;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArraylist = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArraylist.length(); i++) {

                                JSONObject jsonObjectlist = jsonArraylist.getJSONObject(i);
                                String thu = jsonObjectlist.getString("dt");
                                // gán và đổi giá trị datetime
                                long l = Long.valueOf(thu);
                                Date date = new Date(l * 1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("\t\tEEEE \n dd-MM-yyyy");
                                String Day = simpleDateFormat.format(date);

                                // lấy dữ liệu nhiệt độ
                                JSONObject jsonObjectTemp = jsonObjectlist.getJSONObject("temp");
                                String max = jsonObjectTemp.getString("max");
                                String min = jsonObjectTemp.getString("min");
                                Double A = Double.valueOf(max);
                                Double B = Double.valueOf(min);
                                String NhietDoMax = String.valueOf(A.intValue());
                                String NhietDoMin = String.valueOf(B.intValue());
                                // gọi đối tượng weather status + icon
                                JSONArray jsonArrayWeather = jsonObjectlist.getJSONArray("weather");
                                JSONObject jsonObjectweather = jsonArrayWeather.getJSONObject(0);
                                String status = jsonObjectweather.getString("description");
                                String icon = jsonObjectweather.getString("icon");

                                mangthoitiet.add(new ThoiTiet(Day, icon, NhietDoMax, NhietDoMin, status));

                            }
                            customAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    private void anhxa() {
//        cbchart=findViewById(R.id.linetemp);
        imgsearch = findViewById(R.id.imgsearch);
        edtSearch = findViewById(R.id.edtSearch);
        tvclouds = findViewById(R.id.tvCloud);
        tvCountry = findViewById(R.id.tvCountry);
        tvName = findViewById(R.id.tvName);
        tvTemp = findViewById(R.id.tvTemp);
        tvStatus = findViewById(R.id.tvStatus);
        tvhumidity = findViewById(R.id.tvhumidity);
        tvwind = findViewById(R.id.tvWind);
        tvday = findViewById(R.id.tvDay);
        imgicon = findViewById(R.id.imgicon);
        tvMax = findViewById(R.id.tvMax);
        tvMin = findViewById(R.id.tvMin);
        thu1=findViewById(R.id.thu);
        lstweather = findViewById(R.id.lstweather);
        fragment_container=findViewById(R.id.fragment_container);
        tvdate = findViewById(R.id.tvdate);
        lineChart = findViewById(R.id.linetemp);

        // gọi ánh xạ custormer
        mangthoitiet = new ArrayList<ThoiTiet>();
        customAdapter= new CustomAdapter(MainActivity.this, mangthoitiet);
        lstweather.setAdapter(customAdapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        switch (item.getItemId()){
//            case R.id.nav_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new History()).commit();
//                break;
//        }
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this,History.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this,"welcome",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_tools) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

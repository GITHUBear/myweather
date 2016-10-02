package com.example.dell.myweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myweather.R;
import com.example.dell.myweather.db.myWeatherDB;
import com.example.dell.myweather.model.City;
import com.example.dell.myweather.model.County;
import com.example.dell.myweather.model.Province;
import com.example.dell.myweather.util.HttpCallbackListener;
import com.example.dell.myweather.util.HttpUtil;
import com.example.dell.myweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/10/2.
 */

public class ChooseAreaActivity extends Activity{
    public static final int PROVINCELEVEL=0;
    public static final int CITYLEVEL=1;
    public static final int COUNTYLEVEL=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private myWeatherDB myweatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dl=new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dl);
        listView.setAdapter(adapter);
        myweatherDB = myWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> args, View view, int index, long arg3) {
                if (level == PROVINCELEVEL) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (level == CITYLEVEL) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        provinceList=myweatherDB.loadProvinces();
        if(provinceList.size()>0){
            dl.clear();
            for(Province province:provinceList){
                dl.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            level=PROVINCELEVEL;
        }
        else{
            queryFromNet(null,"province");
        }
    }
    private void queryCities(){
        cityList=myweatherDB.loadCities(selectedProvince.getId());
        if(provinceList.size()>0){
            dl.clear();
            for(City city:cityList){
                dl.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            level=PROVINCELEVEL;
        }
        else{
            queryFromNet(selectedProvince.getProvinceCode(), "city");
        }
    }
    private void queryCounties(){
        countyList = myweatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dl.clear();
            for (County county : countyList) {
                dl.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            level = COUNTYLEVEL;
        } else {
            queryFromNet(selectedCity.getCityCode(), "county");
        }
    }
    private void queryFromNet(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.solveProvinceResponse(myweatherDB,
                            response);
                } else if ("city".equals(type)) {
                    result = Utility.solveCityResponse(myweatherDB,
                            response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.solveCountiesResponse(myweatherDB,
                            response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
// 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onBackPressed() {
        if (level == COUNTYLEVEL) {
            queryCities();
        } else if (level == CITYLEVEL) {
            queryProvinces();
        } else {
            finish();
        }
    }
}

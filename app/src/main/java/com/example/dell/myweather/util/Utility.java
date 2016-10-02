package com.example.dell.myweather.util;

import android.text.TextUtils;

import com.example.dell.myweather.db.myWeatherDB;
import com.example.dell.myweather.model.City;
import com.example.dell.myweather.model.County;
import com.example.dell.myweather.model.Province;

/**
 * Created by dell on 2016/10/2.
 */

public class Utility {
    //解析服务器返回的数据
    //省级
    public synchronized static boolean solveProvinceResponse(myWeatherDB myweatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null && allProvinces.length>0){
                for(String all:allProvinces){
                    String[] divide=all.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(divide[0]);
                    province.setProvinceName(divide[1]);
                    myweatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    //市级
    public static boolean solveCityResponse(myWeatherDB myweatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null && allCities.length>0){
                for(String all:allCities){
                    String[] divide=all.split("\\|");
                    City city=new City();
                    city.setCityCode(divide[0]);
                    city.setCityName(divide[1]);
                    city.setProvinceId(provinceId);
                    myweatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    //县级
    public static boolean solveCountiesResponse(myWeatherDB myweatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties!=null && allCounties.length>0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    myweatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}

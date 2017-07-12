package br.com.vitorota.naodurmanoponto.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

import br.com.vitorota.naodurmanoponto.MyApp;


/**
 * Created by Vitor Ota on 26/06/2016.
 */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        double lat =-19.512871;
        double lng = -42.611650;

        lat = Double.valueOf(MyApp.getConfig(MyApp.Configs.CONFIG_LATITUDE,lat));
        lng = Double.valueOf(MyApp.getConfig(MyApp.Configs.CONFIG_LONGITUDE,lng));


        double latAtual = location.getLatitude();
        double lngAtual = location.getLongitude();
        Log.i("SErVICE", new Date()+ " - "+ haversine(lat,lng,latAtual,lngAtual));

        Log.i("DISTANCIA", new Date()+ " - "+ haversine(lat,lng,latAtual,lngAtual));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

}

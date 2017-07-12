package br.com.vitorota.naodurmanoponto;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by Vitor Ota on 22/06/2016.
 */
public class MyApp extends Application {
    private static Context appContext;
    private static String PREFERENCES_NAME;
    private static SharedPreferences preferences;
    public final static String SERVICE_NAME = "DespertaService";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        preferences = appContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static <T> T getConfig(Configs config, T defaultValue) {
        Map<String, ?> all = preferences.getAll();

        T obj=defaultValue;
        if(all.containsKey(config.configName)) {
            try {
                obj = (T) all.get(config.configName);
            } catch (Exception e) {
                obj = (T) all.get(config.configName).toString();
            }
        }
        return obj;
    }

    public static void putConfig(Configs config, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if(config.tipo == Boolean.TYPE){
            editor.putBoolean(config.toString(), (boolean) value);
        } else if (config.tipo == Double.TYPE) { //não tem como salvar um double nas configs, salvar como string mesmo
            editor.putString(config.toString(),  value.toString());
        } else if (config.tipo == Long.TYPE) {
            editor.putLong(config.toString(), (long) value);
        }else if (config.tipo == Integer.TYPE) {
            editor.putInt(config.toString(), (int) value);
        }else{
            editor.putString(config.toString(), value.toString());
        }
        editor.apply();
        editor.commit();
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public enum Configs {
        CONFIG_LATITUDE(getAppContext().getString(R.string.config_latitude), Double.TYPE),
        CONFIG_LONGITUDE(getAppContext().getString(R.string.config_longitude), Double.TYPE),
        CONFIG_DISTANCIA(getAppContext().getString(R.string.config_distancia), Integer.TYPE),
        CONFIG_VIBRAR(getAppContext().getString(R.string.config_vibrar), Boolean.TYPE),
        CONFIG_PISCAR(getAppContext().getString(R.string.config_piscar), Boolean.TYPE),
        CONFIG_ATIVADO(getAppContext().getString(R.string.config_ativado), Boolean.TYPE);

        private String configName;
        private Class<?> tipo;

        Configs(String configName, Class<?> tipo) {
            this.configName = configName;
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return configName;
        }
    }

}

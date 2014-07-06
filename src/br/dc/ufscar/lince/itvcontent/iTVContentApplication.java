package br.dc.ufscar.lince.itvcontent;

import android.app.Application;
import android.content.Context;

public class iTVContentApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        iTVContentApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return iTVContentApplication.context;
    }
}
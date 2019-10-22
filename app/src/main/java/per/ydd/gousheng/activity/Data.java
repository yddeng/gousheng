package per.ydd.gousheng.activity;

import android.app.Application;
import android.util.Log;

public class Data extends Application {
    //应用更新信息
    private String updateData ;
    public String getUpdateData(){
        Log.d("TAG", "getUpdateData: "+this.updateData);
        return this.updateData;
    }
    public void setUpdateData(String data){
        Log.d("TAG", "setUpdateData: "+data);
        this.updateData = data;
    }

}

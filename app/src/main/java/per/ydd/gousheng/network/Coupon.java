package per.ydd.gousheng.network;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import per.ydd.gousheng.service.FloatBallService;
import per.ydd.http.HttpClient;

public class Coupon {

    public interface CouponCallBack{
        //内容，链接，error
       void Call(String text , String clickUrl,Exception err);
    }

    private static String apiKey = "fao50lMQ6Qca8nhNlpguD7u68IlxVSnx";
    /**
     * 淘宝
     */
    private  static String tbUrl = "http://api.tbk.dingdanxia.com/tbk/tkl_privilege";
    public static void tbCoupon(String tkl,final CouponCallBack callBack){
        String param;
        try {
            param = "apikey="+ URLEncoder.encode(apiKey,"UTF-8")+"&tkl="+URLEncoder.encode(tkl,"UTF-8");
        }catch (Exception err){
            callBack.Call(null,null,err);
            return;
        }

        HttpClient.Post(tbUrl, param, new HttpClient.CallBack() {
                @Override
                public void Call(String result, Exception err) {
                    if (err == null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Integer code = jsonObject.getInt("code");
                            if (code == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String coupon_info, coupon_click_url;
                                String max_commission_rate;
                                String text,item_url;

                                if ((!data.isNull("coupon_info") && !data.isNull("coupon_click_url")) &&
                                        (!data.isNull("max_commission_rate"))){ //有卷有佣金
                                    coupon_info = data.getString("coupon_info");
                                    coupon_click_url = data.getString("coupon_click_url");
                                    //max_commission_rate = Float.parseFloat(data.getString("max_commission_rate"));
                                    //max_commission_rate = data.getString("max_commission_rate");
                                    text = "点击领取 " + coupon_info + "卷";//+max_commission_rate;
                                    callBack.Call(text,coupon_click_url,null);
                                }else  if ((!data.isNull("coupon_info") && !data.isNull("coupon_click_url")) &&
                                        (data.isNull("max_commission_rate"))) { //有卷无佣金
                                    coupon_info = data.getString("coupon_info");
                                    coupon_click_url = data.getString("coupon_click_url");
                                    text = "点击领取 " + coupon_info + "卷";
                                    callBack.Call(text,coupon_click_url,null);
                                }else if ((data.isNull("coupon_info") || data.isNull("coupon_click_url")) &&
                                        (!data.isNull("max_commission_rate"))) {//无卷有佣金
                                    //max_commission_rate = data.getString("max_commission_rate");
                                    item_url = data.getString("item_url");
                                    text = "点击这里直接购买";//+max_commission_rate;
                                    callBack.Call(text,item_url,null);
                                }else {//无卷无佣金
                                    callBack.Call("当前宝贝暂无优惠券",null,null);
                                }
                            } else if (code == -1){ //没有参加活动
                                Log.d("TAG", "code: " + code + jsonObject.getString("msg"));
                                callBack.Call("当前宝贝暂无优惠券",null,null);
                            }else { //请求错误
                                Log.d("TAG", "code: " + code +"msg: "+ jsonObject.getString("msg"));
                                String text = "code: " + code +"msg: "+ jsonObject.getString("msg");
                                callBack.Call(text,null,null);
                            }
                        }catch (Exception e){
                            Log.d("TAG", "couponText: "+e.toString());
                            callBack.Call(null,null,e);
                        }
                    } else {
                        callBack.Call(null,null,err);
                    }
                }
        });

    }

    /**
     * 京东
     */

    private static String jdUrl = "http://api.tbk.dingdanxia.com/jd/query_goods";
    public static  void jdCoupon(String itemUrl,final CouponCallBack callBack){
        String param;
        try {
            param = "apikey="+ URLEncoder.encode(apiKey,"UTF-8")+"&skuIds="+URLEncoder.encode(jdParseUrlToID(itemUrl),"UTF-8");
        }catch (Exception err){
            callBack.Call(null,null,err);
            return;
        }

        HttpClient.Post(jdUrl, param, new HttpClient.CallBack() {
            @Override
            public void Call(String result, Exception err) {
                if (err == null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Integer code = jsonObject.getInt("code");
                        if (code == 200) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0){
                                JSONObject item = data.getJSONObject(0);
                                String coupon = jdItemCoupon(item);
                                String commission = jdItemCommission(item);
                                if (!TextUtils.isEmpty(coupon) && !TextUtils.isEmpty(commission)){
                                    String text = coupon +","+commission;
                                    callBack.Call(text,null,null);
                                }
                            }

                        } else if (code == -1){ //没有参加活动
                            Log.d("TAG", "code: " + code + jsonObject.getString("msg"));
                            callBack.Call("当前宝贝暂无优惠券",null,null);
                        }else { //请求错误
                            Log.d("TAG", "code: " + code +"msg: "+ jsonObject.getString("msg"));
                            String text = "code: " + code +"msg: "+ jsonObject.getString("msg");
                            callBack.Call(text,null,null);
                        }
                    }catch (Exception e){
                        Log.d("TAG", "couponText: "+e.toString());
                        callBack.Call(null,null,e);
                    }
                } else {
                        //callBack.Call(null,err);
                }
            }
        });
    }

    private static String jdParseUrlToID(String jdItemUrl){
        String s1=jdItemUrl.substring(jdItemUrl.lastIndexOf("/")+1);
        String[] s2 = TextUtils.split(s1,".html");
        String ret = s2[0];
        Log.d("TAG", "jdParseUrlToID: "+ret);
        return ret;
    }

    private static  String jdItemCommission(JSONObject item){
        try{
            if (!item.isNull("commissionInfo")){
                JSONObject info = item.getJSONObject("commissionInfo");
                if (!info.isNull("commission")){
                    return "佣金:"+info.getDouble(info.getString("sommission"));
                }
            }
            return null;
        }catch (Exception err){
            return null;
        }
    }

    private static String jdItemCoupon(JSONObject item){
        try{
            if (!item.isNull("couponInfo")){
                JSONObject info = item.getJSONObject("couponInfo");
                if (!info.isNull("couponList")){
                    JSONArray list = info.getJSONArray("couponList");
                    String ret = "";
                    for (int i = 0;i < list.length();i++){
                        JSONObject object= list.getJSONObject(i);
                        if (!object.isNull("isBest") && object.getInt("isBest") == 1){
                            Double discount,quota;
                            discount = object.getDouble("discount");
                            quota = object.getDouble("quota");
                            ret = "满"+quota+"元减"+discount+"元券";
                        }
                    }
                    if (!TextUtils.isEmpty(ret)){
                        return ret;
                    }
                }
            }
            return null;
        }catch (Exception err){
            return null;
        }
    }
}

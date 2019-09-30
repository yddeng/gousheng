package pre.ydd.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    public interface CallBack{
        void Call(String result,Exception err);
    }

    // 链接超时时间（毫秒）
    private int ConnectTimeout;
    //设置读取超时时间（毫秒）
    private int ReadTimeout;

    public HttpClient(){
        this.ConnectTimeout = 5000;
        this.ReadTimeout = 5000;
    }

    public void Get(final String reqUrl,final String data,  final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(String.format("%s?%s",reqUrl,data ));
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(ConnectTimeout);
                    connection.setReadTimeout(ReadTimeout);

                    //返回输入流
                    InputStream in = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    callBack.Call(response.toString(),null);
                } catch (Exception e) {
                    callBack.Call(null,e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /*
    public String SyncGet(String reqUrl) throws Exception{
        final String ret = null;
        final Exception err = null;
        final CountDownLatch latch = new CountDownLatch(1);

        Get(reqUrl, new CallBack() {
            @Override
            public void Call(String result, Exception e) {
               // ret = result;
                latch.countDown();
            }
        });

        if ( err != null){
            throw err;
        }
        Log.d("SyncGet", "Call: 222");
        latch.await();
        Log.d("SyncGet", "Call: 333");
        return ret;
    }
*/

    public void Post(final String reqUrl,final String data, final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(reqUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(ConnectTimeout);
                    connection.setReadTimeout(ReadTimeout);

                    //
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length()+"");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);

                    //返回输入流
                    InputStream in = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    callBack.Call(response.toString(),null);
                } catch (Exception e) {
                    callBack.Call(null,e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}

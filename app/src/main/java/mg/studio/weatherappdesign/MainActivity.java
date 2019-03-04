package mg.studio.weatherappdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date=new Date();
        TimeZone time = TimeZone.getTimeZone("ETC/GMT-13");
        TimeZone.setDefault(time);
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("MM/dd/yyyy");
        ((TextView) findViewById(R.id.tv_date)).setText(format2.format(new Date()) );

        int today=date.getDay();
        ((TextView) findViewById(R.id.weekday)).setText(Week(today));
        ((TextView) findViewById(R.id.dt_fr)).setText(Weeks(today+1));
        ((TextView) findViewById(R.id.dt_se)).setText(Weeks(today+2));
        ((TextView) findViewById(R.id.dt_th)).setText(Weeks(today+3));
        ((TextView) findViewById(R.id.dt_fo)).setText(Weeks(today+4));
    }

    public String Week(int day){
        String Day=null;
        String Week[]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        Day=Week[day];
        return Day;
    }

    public String Weeks(int d){
        if(d>6)
            d=0;
        String Day=null;
        String Week[]={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        Day=Week[d];
        return Day;
    }




    public int ChangePic(String wea){
        if(wea.contains("晴"))
            return R.drawable.sunny_small;
        else if(wea.contains("阴"))
            return   R.drawable.partly_sunny_small;
        else if(wea.contains("雨"))
            return  R.drawable.rainy_small;
        else
            return  R.drawable.windy_small;
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    public void btnClick(View view) {
        if(isNetworkAvailable(MainActivity.this)) {
            new DownloadUpdate().execute();
            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this,"No Internet",Toast.LENGTH_SHORT).show();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://wthrcdn.etouch.cn/weather_mini?city=重庆";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String temperature) {
            //Update the temperature displayed
            //((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
            String min=null;
            String loc=null;
            String type=null;
            String type2=null;
            String type3=null;
            String type4=null;
            String type5=null;
            try{
                JSONObject jsonObject=new JSONObject(temperature);
                JSONObject detail=jsonObject.getJSONObject("data");
                JSONArray js=(JSONArray)detail.get("forecast");
                min=detail.get("wendu").toString();
               // String regEx="[^0-9]";
                //Pattern p = Pattern.compile(regEx);
                //Matcher m = p.matcher(min);
                //min=m.replaceAll("").trim();

                loc=detail.getString("city");

                type=js.getJSONObject(0).getString("type");
                type2=js.getJSONObject(1).getString("type");
                type3=js.getJSONObject(2).getString("type");
                type4=js.getJSONObject(3).getString("type");
                type5=js.getJSONObject(4).getString("type");


            }catch (Exception e){
                e.printStackTrace();
            }
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(min);
            ((TextView) findViewById(R.id.tv_location)).setText(loc);
            ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(ChangePic(type)));
            ((ImageView)findViewById(R.id.im_fr)).setImageDrawable(getResources().getDrawable(ChangePic(type2)));
            ((ImageView)findViewById(R.id.im_se)).setImageDrawable(getResources().getDrawable(ChangePic(type3)));
            ((ImageView)findViewById(R.id.im_th)).setImageDrawable(getResources().getDrawable(ChangePic(type4)));
            ((ImageView)findViewById(R.id.im_fo)).setImageDrawable(getResources().getDrawable(ChangePic(type5)));
        }
    }


}

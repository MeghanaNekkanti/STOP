package meghana.rohan.saferworld;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rohan on 4/17/2016.
 */
public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static LocationRequest locationRequest;
    static GoogleApiClient googleApiClient;
    public static Location loc;
    String Response = "";
    boolean first = true;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // connect to play services to get location
        connectToGoogleApi();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((loc != null) /*&& (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id", "").equals(""))*/) {
                    sendLoc(loc);
                    Log.d("loc test", "" + loc);
                }

            }
        }, 1000, 10000);

        return START_STICKY;
    }


    public void connectToGoogleApi() {

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
                Log.d("TAG", "connect");
            }
        } else {
            Log.e("TAG", "unable to connect to google play services.");
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "connected");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        loc = location;
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendLoc(final Location location) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... params) {

                URL url;
                try {
                    if (first) {
                        first = false;
                        url = new URL("http://172.20.172.184:3000/insert2");
                    } else {
                        url = new URL("http://172.20.172.184:3000/update");
                    }

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("id", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id", ""))
                            .appendQueryParameter("lat", loc.getLatitude() + "")
                            .appendQueryParameter("long", loc.getLongitude() + "")
                            .appendQueryParameter("time", getCurrentTimeStamp());

                    Log.d("id", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id", ""));


                    String query = builder.build().getEncodedQuery();

                    Log.d("test", query);

                    OutputStream os = httpURLConnection.getOutputStream();

                    BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    mBufferedWriter.write(query);
                    mBufferedWriter.flush();
                    mBufferedWriter.close();
                    os.close();

                    httpURLConnection.connect();
                    BufferedReader mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String inline;
                    while ((inline = mBufferedInputStream.readLine()) != null) {
                        Response += inline;
                    }
                    mBufferedInputStream.close();
                    Log.d("response", Response);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Response = "";
                return null;
            }
        }.execute();
    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


}
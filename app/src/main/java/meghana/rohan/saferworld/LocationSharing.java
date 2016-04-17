package meghana.rohan.saferworld;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rohan on 4/17/2016.
 */
public class LocationSharing extends Fragment implements OnMapReadyCallback {
    View view;
    private GoogleMap mMap;
    String Response = "";
    ArrayList<Marker> markers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_location_sharing, container, false);
        // do something here
        SupportMapFragment mapFragment = (SupportMapFragment) /*getActivity().getSupportFragmentManager()*/this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new getLoc().execute();
                Log.d("testing","testing");
            }
        }, 1000, 11000);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class getLoc extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (Marker mMarker : markers) {
                mMarker.remove();
            }
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
            markers.add(marker);

        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            try {
                url = new URL("http://172.20.172.184:3000/retrieve");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("ec", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("id", ""));


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
            } catch (NullPointerException e) {

            }
            parseJson(Response);
            Response = "";

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    void parseJson(String response) {
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                lat = object.getString("lat");
                lng = object.getString("long");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String lat;
    String lng;
}

package meghana.rohan.saferworld;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class SelectFriends extends AppCompatActivity {

    ArrayList<Boolean> selected = new ArrayList<>();
    ListView mListView;
    JSONArray data;
    ArrayList<String> id = new ArrayList<>(), name = new ArrayList<>();
    String Response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            data = new JSONArray(preferences.getString("friendList", ""));
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                id.add(obj.getString("id"));
                name.add(obj.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SelectFriends.this, android.R.layout.simple_list_item_checked, name);

        mListView.setAdapter(arrayAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < name.size(); i++)
            selected.add(false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selected.get(position))
                    selected.add(position, false);
                else
                    selected.add(position, true);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                for (int i = 0; i < name.size(); i++) {
                    if (selected.get(i)) {
                        Log.d("selected", "name:" + name.get(i) + " id:" + id.get(i));
                        new SendData(id.get(i)).execute();
                    }
                }
                startActivity(new Intent(SelectFriends.this,Key.class));
//                final String key = ""+(int)(Math.random() * 9000+1000);
//
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//                builder.setMessage("Share this key with emergency contacts:"+key).setCancelable(
//                        false).setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putString("key",key);
//                                editor.putString("login","true");
//                                editor.apply();
//
//                                startActivity(new Intent(SelectFriends.this, MainActivity.class));
//                                finish();
//                            }
//                        });
//                android.app.AlertDialog alert = builder.create();
//                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SendData extends AsyncTask<Void, Void, Void> {

        String ec = "";

        SendData(String ec) {
            this.ec = ec;
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            try {
                url = new URL("http://172.20.172.184:3000/insert1");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id", ""))
                        .appendQueryParameter("name", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name", ""))
                        .appendQueryParameter("ec", ec);


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
    }

}

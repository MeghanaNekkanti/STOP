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
import android.widget.Button;
import android.widget.EditText;

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

/**
 * Created by Rohan on 4/17/2016.
 */
public class PostRide extends Fragment {

    View view;
    EditText to,from,when;
    Button post;
    String Response = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_ride,container,false);

        to = (EditText) view.findViewById(R.id.to);
        from = (EditText) view.findViewById(R.id.from);
        when = (EditText) view.findViewById(R.id.time);
        post = (Button) view.findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (to.getText().toString().isEmpty()){
                    to.setError("Cannot be empty");
                    to.requestFocus();
                    return;
                }
                if (from.getText().toString().isEmpty()){
                    from.setError("Cannot be empty");
                    from.requestFocus();
                    return;
                }
                if (when.getText().toString().isEmpty()){
                    when.setError("Cannot be empty");
                    when.requestFocus();
                    return;
                }
                new SendRide(to.getText().toString(),from.getText().toString(),when.getText().toString()).execute();
            }
        });

        return view;
    }

    public class SendRide extends AsyncTask<Void,Void,Void>{
        String to,from,when;
        SendRide(String to,String from,String when){
            this.to=to;
            this.from=from;
            this.when=when;
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            try {
                url = new URL("http://172.20.172.184:3000/shareride");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("name",""))
                        .appendQueryParameter("to", to)
                        .appendQueryParameter("when", when)
                        .appendQueryParameter("from", from);


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

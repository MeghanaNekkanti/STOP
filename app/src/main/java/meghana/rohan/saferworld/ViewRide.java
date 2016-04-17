package meghana.rohan.saferworld;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

/**
 * Created by Rohan on 4/17/2016.
 */
public class ViewRide extends Fragment {

    View view;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter salesmanRecyclerViewAdapter;
    String Response = "";
    ArrayList<String> nameAL = new ArrayList<>(),fromAL = new ArrayList<>(),toAL = new ArrayList<>(), whenAL = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_ride, container, false);

        setUpRecyclerView(view);

        new GetRides().execute();

        return view;
    }

    private void setUpRecyclerView(View v) {

        recyclerView = (RecyclerView) v.findViewById(R.id.recList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        salesmanRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(salesmanRecyclerViewAdapter);
    }


    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyHolder> {
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(getActivity()).inflate(R.layout.single_view, parent, false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
           try {


               holder.to.setText(toAL.get(position));
               holder.from.setText(fromAL.get(position));
               holder.when.setText(whenAL.get(position));
               holder.name.setText(nameAL.get(position));
               clickCallback(holder, position);
           }catch (IndexOutOfBoundsException e){

           }
        }

        private void clickCallback(MyHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }

        @Override
        public int getItemCount() {
            return nameAL.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            TextView from, to, when, name;

            public MyHolder(View itemView) {
                super(itemView);
                from = (TextView) itemView.findViewById(R.id.fromTv);
                to = (TextView) itemView.findViewById(R.id.toTv);
                when = (TextView) itemView.findViewById(R.id.whenTv);
                name = (TextView) itemView.findViewById(R.id.name);

            }
        }
    }

    public class GetRides extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            salesmanRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL url;
            try {
                url = new URL("http://172.20.172.184:3000/getride");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("id", ""));

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

            parseJson(Response);
            Response = "";
            return null;
        }
    }

    void parseJson(String Response){
        try {
            JSONArray array = new JSONArray(Response);
            for (int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                fromAL.add(obj.getString("from"));
                toAL.add(obj.getString("to"));
                whenAL.add(obj.getString("when"));
                nameAL.add(obj.getString("Uid"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

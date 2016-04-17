package meghana.rohan.saferworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    static CallbackManager callbackManager;
    static LoginButton login;
    String TAG = "FACEBOOKSDK";
    public static String friend_count = "", id = "", name = "";
    public static ArrayList<String> friends_list = new ArrayList<>();
    boolean friend_gotData = false;
    EditText ec1,ec2;
    JSONArray friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton) findViewById(R.id.login_button);

        // set the required permissions
        login.setReadPermissions("public_profile email user_friends");

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("whatisaccesstoken2", LoginManager.getInstance() + " " + AccessToken.getCurrentAccessToken());
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();

                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
            }


        });

        ec1= (EditText) findViewById(R.id.EC1);
        ec2= (EditText) findViewById(R.id.EC2);


    }

    private void RequestData() {

        // to get the count of the friends and the list of friends using the app
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d(TAG, "friends count plus list " + response.getJSONObject());
                            JSONObject resp = new JSONObject("" + response.getJSONObject());
                            JSONArray data = resp.getJSONArray("data");
                            friendsList = data;
                            JSONObject summary = resp.getJSONObject("summary");
                            friend_count = summary.getString("total_count");
                            Log.d(TAG, "" + friend_count);

                            JSONObject friends;
                            for (int i = 0; i < data.length(); i++) {
                                friends = data.getJSONObject(i);
                                friends_list.add(friends.getString("name"));
                            }
                            Log.d(TAG, "friends test " + friends_list);
                            friend_gotData = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        // to get basic information of the user
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                try {
                    id = jsonObject.getString("id");
                    name = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();


    }


    public void submit(View view) {
        if (ec1.getText().toString().isEmpty()){
            ec1.setError("Cannot be empty");
            ec1.requestFocus();
            return;
        }
        if (friend_gotData) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("friendList",friendsList.toString());
            editor.putString("id",id);
            editor.putString("name",name);
            editor.putString("ec1",ec1.getText().toString());
            editor.putString("ec2",ec2.getText().toString());
            editor.apply();
            Log.d("id test",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id", ""));
            startActivity(new Intent(SignupActivity.this, SelectFriends.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}

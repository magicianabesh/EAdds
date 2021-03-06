package studio.androidapp.suj.e_adds.Audio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import studio.androidapp.suj.e_adds.Picture.picture_list;
import studio.androidapp.suj.e_adds.R;
import studio.androidapp.suj.e_adds.Helper.SharedPrefManager;
import studio.androidapp.suj.e_adds.Helper.URL;
import studio.androidapp.suj.e_adds.Helper.User;
import studio.androidapp.suj.e_adds.Helper.VolleySingleton;

public class AudioAdd extends AppCompatActivity {
    private ProgressDialog pDialog;
    private List<picture_list> audioList = new ArrayList<picture_list>();
    private ListView listView;
    private CustomAudioList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please wait while the add loads completely...");
        pDialog.show();

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                picture_list text = (picture_list) listView.getItemAtPosition(arg2);
                String abc = text.getTitle();

                String cde = text.getCost();
                String audio_name = text.getAudio();

                Toast.makeText(AudioAdd.this,("You chose "+abc+" add.Here are the details."),Toast.LENGTH_LONG).show();
                Intent i = new Intent(AudioAdd.this,SingleAudio.class);
                i.putExtra("title",abc);
                i.putExtra("cost",cde);
                i.putExtra("audio_name",audio_name);
                startActivity(i);
            }
        });
        User user = SharedPrefManager.getInstance(this).getUser();
        final String location = user.getLocation();
        final String age = user.getAge();
        final String sex = user.getSex();
        final String ead_token = "ead_nepal_tokan_w345392759";
        adapter = new CustomAudioList(this, audioList);
        listView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.AUDIO_URL,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String json_response) {
                        //converting response to json object


                        try {
                            JSONObject obj = new JSONObject(json_response);


                            final String abc = obj.getString("status");
                            final String a = obj.getString("status_message");

                            if (a.equalsIgnoreCase("success")) {
                                Toast.makeText(getApplicationContext(), "these are the current list of your audio add", Toast.LENGTH_SHORT).show();



                                JSONArray audioarray = obj.getJSONArray("data");
                                for(int i=0;i<audioarray.length();i++) {
                                    JSONObject audio = audioarray.getJSONObject(i);
                                    String id = audio.getString("id");
                                    String title=   audio.getString("a_title");
                                    String audio_name = audio.getString("audio");
                                    String cost =  audio.getString("reach_out_price");
                                    String client =  audio.getString("fk_client_id");

                                    picture_list audio_detail = new picture_list();
                                    audio_detail.setId(id);
                                    audio_detail.setTitle(title);
                                    audio_detail.setAudio(audio_name);
                                    audio_detail.setCost(cost);
                                    audio_detail.setClient(client);

                                    audioList.add(audio_detail);
                                }

                                //getting the user from the response


                            } else {
                                Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        //  Toast.makeText(getApplicationContext(), "Sorry! Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ead_token",ead_token);
                params.put("location", location);
                params.put("age", age);
                params.put("sex", sex);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

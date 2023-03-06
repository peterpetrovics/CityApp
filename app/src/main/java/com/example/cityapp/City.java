package com.example.cityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class City extends AppCompatActivity {

     Spinner spinner;
     Button searchButton;
     Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);

        //Hozzáadjuk a menühöz az 5db várost
        spinner = findViewById(R.id.spinner);
        String[] arraySpinner = new String[] {
                "Nincs város kiválasztva","Debrecen", "Budapest", "Pécs", "Miskolc", "Sopron"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);


        searchButton = findViewById(R.id.searchButton);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Megfogalmazzuk a kérés URL címét, a spinnertext a város neve, melyet hozzáfűzünk várostól függve az alap linkhez
                String spinnertext = spinner.getSelectedItem().toString();
                RequestQueue queue = Volley.newRequestQueue(context);
                String apiKey = getString(R.string.geodb_api_key);
                String url = "";
                if (spinnertext.equals("Nincs város kiválasztva")) {
                    Toast.makeText(City.this, "Válasszon egy várost a listából!", Toast.LENGTH_SHORT).show();
                    return;

                }
                //Budapestnél az 1.elemet kell venni, ami lényegében már a 2. a listában, mert az első a 19.kerület
                else if (spinnertext.equals("Budapest")) {
                     url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=5&offset=1&namePrefix=" + spinnertext;
                }
                else {
                     url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=5&offset=0&namePrefix=" + spinnertext;
                }


                //JsonObject segítségével kérést indítunk
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    //Az adatokat szétszedjük városnév, illetve a 2 koordináta alapján
                                    JSONArray data = response.getJSONArray("data");
                                    JSONObject city = data.getJSONObject(0);
                                    String cityName = city.getString("city");
                                    double latitude = city.getDouble("latitude");
                                    double longitude = city.getDouble("longitude");

                                    //A Json objektum feldolgozása után elindíthatjuk a térképet, melyhez az openMap metódust hívjuk meg
                                    openMap(cityName, latitude, longitude);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                         new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                    @Override

                    //A kérés fejrészéhez hozzáadjuk az api kulcsot, mellyel az alkalmazásunket hitelesítjük
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-RapidAPI-Key", apiKey);
                        return headers;
                    }
                };
                //Elküldjük a kérést
                queue.add(jsonObjectRequest);
            }
        });


    }

    private void openMap(String cityName, double latitude, double longitude) {
        //Intent lérehozása, mely a google térképet indítja el
        //Az Uri (Uniform Resource Identifier a Google térkép elindításához szükséges, mely az alábbi formátumú
        //geo:latitude,longitude?q=encoded_query
        Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(cityName));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");

        //Ellenőrizzük, hogy telepítve van-e a google térkép
        PackageManager packageManager = getPackageManager();
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "A Google térkép nincs telepítve", Toast.LENGTH_SHORT).show();
        }


    }


}

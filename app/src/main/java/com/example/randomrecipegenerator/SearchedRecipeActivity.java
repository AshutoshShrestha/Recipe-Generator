package com.example.randomrecipegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeActivity extends AppCompatActivity {
    List<String> title  = new ArrayList<>();
    List<String> imgURL = new ArrayList<>();
    String recipes_string_data = "";

    private static String BASE_URL = "https://api.spoonacular.com/recipes/random?number=10&tags=";
    private static String API_KEY = "YOUR_API_KEY";

    public SearchedRecipeActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searched_recipes_activity);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String searchParam = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(searchParam!=null) {
            Log.d("Search param:", searchParam);
            try {
                makeAPIRequest(searchParam.replaceAll("\\s", ""));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("Save", "Saving loaded recipes");
        savedInstanceState.putString("RECIPE_DATA", recipes_string_data);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        this.recipes_string_data = savedInstanceState.getString("RECIPE_DATA");
        Log.d("Restore", recipes_string_data);
        SearchedRecyclerAdapter recyclerAdapter = null;
        RecyclerView searchedRecipes = findViewById(R.id.search_result);
        try {
            recyclerAdapter = new SearchedRecyclerAdapter(SearchedRecipeActivity.this, title, imgURL, new JSONObject(recipes_string_data));
            searchedRecipes.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            searchedRecipes.setAdapter(recyclerAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseData(JSONObject recipesObj) {
        JSONArray recipesData = new JSONArray();
        Log.d("Recipe tag: ", recipesObj.toString());
        try {
            recipesData = recipesObj.getJSONArray("recipes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("NumberOfRecipes", String.valueOf(recipesData.length()));
        for(int i = 0; i<recipesData.length(); i++) {
            try {
                title.add(recipesData.getJSONObject(i).getString("title"));
                if(recipesData.getJSONObject(i).has("image")) {
                    imgURL.add(recipesData.getJSONObject(i).getString("image"));
                }
                else{
                    imgURL.add("NA");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeAPIRequest(String query){
        TextView errorMsg = findViewById(R.id.error_message);
        JSONObject recipesData = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = BASE_URL + query + "&apiKey=" + API_KEY;
        RecyclerView searchedRecipes = findViewById(R.id.search_result);
        Log.d("URL", url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        parseData(response);
                        try {
                            if(response.getJSONArray("recipes").length()==0){
                                errorMsg.setText("No such recipes available.");
                            }
                            else {
                                recipes_string_data += response.toString();
                                errorMsg.setText(null);
                                Log.d("TAG", "Displaying recycler.");
    //                         setting up the recyclerView
                                SearchedRecyclerAdapter recyclerAdapter = null;
                                recyclerAdapter = new SearchedRecyclerAdapter(SearchedRecipeActivity.this, title, imgURL, new JSONObject(recipes_string_data));
                                searchedRecipes.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                                searchedRecipes.setAdapter(recyclerAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RecyclerView searchedRecipes = findViewById(R.id.search_result);
                searchedRecipes.setAdapter(null);
                errorMsg.setText("Oops! Something went wrong.");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}

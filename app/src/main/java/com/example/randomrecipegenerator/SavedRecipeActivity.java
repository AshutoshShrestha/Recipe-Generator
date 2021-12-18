package com.example.randomrecipegenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipeActivity extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String deviceAppUID;

    JSONArray saved_recipes_data = new JSONArray();
    List<String> recipe_name = new ArrayList<String>();
    List<String> img_url = new ArrayList<String>();
    List<Integer> prep_time = new ArrayList<Integer>();

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                    // Handle the Intent
                }
            }
        });

    SavedRecyclerAdapter recyclerAdapter;
    RecyclerView savedRecipes;

    public SavedRecipeActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recipes_activity);

        recyclerAdapter = new SavedRecyclerAdapter(SavedRecipeActivity.this, recipe_name, img_url, prep_time, saved_recipes_data, mStartForResult);
        savedRecipes = findViewById(R.id.saved_recipes);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // for getting data.
        getdata();
    }

    private void getdata() {
        deviceAppUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceAppUID==null) {
            Toast.makeText(SavedRecipeActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
        rootNode = FirebaseDatabase.getInstance("https://randomrecipegenerator-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = rootNode.getReference("recipes").child(deviceAppUID);

        // calling add value event listener method
        // for getting the values from database.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    //create JSONArray of JSONObjects
                    JSONObject recipe = new JSONObject();
                    try {
                        recipe.put("id", ds.child("id").getValue(Long.class));
                        recipe.put("title", ds.child("title").getValue(String.class));
                        recipe.put("img_url", ds.child("img_url").getValue(String.class));
                        recipe.put("prep_time", ds.child("preparation_time").getValue(Integer.class));
                        recipe.put("ingredients", ds.child("ingredient_details").getValue(String.class));
                        recipe.put("preparation", ds.child("preparation_details").getValue(String.class));
                        recipe.put("is_vegan", ds.child("is_vegan").getValue(Boolean.class));
                        recipe.put("is_vegetarian", ds.child("is_vegetarian").getValue(Boolean.class));
                        recipe.put("is_gluten_free", ds.child("is_gluten_free").getValue(Boolean.class));
                        recipe.put("is_dairy_free", ds.child("is_dairy_free").getValue(Boolean.class));
                        recipe_name.add(ds.child("title").getValue(String.class));
                        img_url.add(ds.child("img_url").getValue(String.class));
                        prep_time.add(ds.child("preparation_time").getValue(Integer.class));
                        saved_recipes_data.put(recipe);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                displayRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(SavedRecipeActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void displayRecycler(){
        Log.d("SavedRecycler", "Displaying recycler");
        Log.d("Saved Recipes", saved_recipes_data.toString());
        savedRecipes.setLayoutManager(new LinearLayoutManager(SavedRecipeActivity.this));
        savedRecipes.setAdapter(recyclerAdapter);
    }

}

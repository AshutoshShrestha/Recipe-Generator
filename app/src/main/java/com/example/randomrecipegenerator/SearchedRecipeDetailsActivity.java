package com.example.randomrecipegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchedRecipeDetailsActivity extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String deviceAppUID;

    String r_title, r_ingredient_details, r_preparation_details, r_img_url;
    Boolean r_is_vegetarian, r_is_vegan, r_is_gluten_free, r_is_dairy_free;
    int r_id, r_preparation_time;

    public SearchedRecipeDetailsActivity() throws JSONException {
    }

    public void goBack(View view){
        Log.d("onBackPressed: ", "Button pressed");
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searched_recipe_details_activity);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // Capture the layout's TextView and set the string as its text
        TextView title = findViewById(R.id.recipe_title);
        ImageView img_src = findViewById(R.id.recipe_image);
        TextView is_vegetarian = findViewById(R.id.is_vegetarian);
        TextView is_vegan = findViewById(R.id.is_vegan);
        TextView is_gluten_free = findViewById(R.id.is_gluten_free);
        TextView is_dairy_free = findViewById(R.id.is_dairy_free);
        TextView preparation_time = findViewById(R.id.preparation_time);
        TextView ingredient_details = findViewById(R.id.ingredients_details);
        TextView preparation_details = findViewById(R.id.preparation_details);

        try {
            JSONObject recipe = new JSONObject(getIntent().getStringExtra("RECIPE_DATA"));
            Log.d("RECIPTAG", recipe.toString());
            if(recipe.has("image") && getIntent().hasExtra("IMAGE")) {
                byte[] image_in_bytes = getIntent().getByteArrayExtra("IMAGE");
                Bitmap image_src = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);
                img_src.setImageBitmap(image_src);
                this.r_img_url = recipe.getString("image");
            }
            else{
                this.r_img_url ="NA";
            }
            JSONArray ingredientDetails = recipe.getJSONArray("extendedIngredients");
            JSONArray preparationDetails = recipe.getJSONArray("analyzedInstructions");
            this.r_id = recipe.getInt("id");
            this.r_title = recipe.getString("title");
            title.setText(r_title);
            if(recipe.getBoolean("vegan")) {
                this.r_is_vegan = true;
                is_vegan.setText("Vegan: Yes");
            }
            else{
                this.r_is_vegan = false;
                is_vegan.setText("Vegan: No");
            }
            if(recipe.getBoolean("vegetarian")) {
                this.r_is_vegetarian = true;
                is_vegetarian.setText("Vegetarian: Yes");
            }
            else{
                this.r_is_vegetarian = false;
                is_vegetarian.setText("Vegetarian: No");
            }
            if(recipe.getBoolean("glutenFree")) {
                this.r_is_gluten_free = true;
                is_gluten_free.setText("Gluten Free: Yes");
            }
            else{
                this.r_is_gluten_free = false;
                is_gluten_free.setText("Gluten Free: No");
            }
            if(recipe.getBoolean("dairyFree")) {
                this.r_is_dairy_free = true;
                is_dairy_free.setText("Dairy Free: Yes");
            }
            else{
                this.r_is_dairy_free = false;
                is_dairy_free.setText("Dairy Free: No");
            }
            this.r_preparation_time = recipe.getInt("readyInMinutes");
            preparation_time.setText("Ready in " + this.r_preparation_time + " min");

            String ingredientDetailsString = "";
            for(int i = 0; i<ingredientDetails.length(); i++){
                ingredientDetailsString += String.valueOf(i+1) + ". " + ingredientDetails.getJSONObject(i).getString("originalString") + ".\n";
            }
            this.r_ingredient_details = ingredientDetailsString;
            ingredient_details.setText(this.r_ingredient_details);

            String preparationDetailsString = "";
            JSONArray instructionStep = new JSONArray();
            int countSteps = 1;
            for(int i = 0; i<preparationDetails.length(); i++){
                instructionStep = preparationDetails.getJSONObject(i).getJSONArray("steps");
                for(int j = 0; j<instructionStep.length(); j++){
                    preparationDetailsString += String.valueOf(countSteps) + ". " + instructionStep.getJSONObject(j).getString("step") + "\n";
                    countSteps++;
                }
            }
            this.r_preparation_details = preparationDetailsString;
            preparation_details.setText(this.r_preparation_details);

            Log.d("Preparation", preparationDetailsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public void saveRecipe(View v){
        rootNode = FirebaseDatabase.getInstance("https://randomrecipegenerator-default-rtdb.asia-southeast1.firebasedatabase.app");

        deviceAppUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceAppUID==null) {
            Toast.makeText(SearchedRecipeDetailsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
        Log.d("DeviceID", deviceAppUID);
        reference = rootNode.getReference("recipes").child(deviceAppUID);

        //DeviceID:8b6e78236b288628
        Recipe recipe = new Recipe(this.r_id,
                this.r_title,
                this.r_ingredient_details,
                this.r_preparation_details,
                this.r_img_url,
                this.r_is_vegetarian,
                this.r_is_vegan,
                this.r_is_gluten_free,
                this.r_is_dairy_free,
                this.r_preparation_time);
        reference.child(String.valueOf(recipe.getId())).setValue(recipe);
        Toast.makeText(SearchedRecipeDetailsActivity.this, "Recipe Saved.", Toast.LENGTH_LONG).show();
    }
}

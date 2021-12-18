package com.example.randomrecipegenerator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SavedRecipeDetailsActivity extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    int recipe_id;
    String deviceAppUID;

    // declaring width and height for our PDF file.
    int pageHeight = 1120;
    int pagewidth = 792;
    int pageNum = 1;
    // for storing our images
    Bitmap image_src, scaled_image_src;
    // recipe details
    String recipe_title, recipe_ingredients, recipe_preparation_steps;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;


    public SavedRecipeDetailsActivity() throws JSONException {
    }

    @Override
    public void onBackPressed(){
        Log.d("onBackPressed: ", "Button pressed");
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recipe_details_activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            TextView title = findViewById(R.id.s_recipe_title);
            ImageView img_src = findViewById(R.id.s_recipe_image);
            TextView is_vegetarian = findViewById(R.id.s_is_vegetarian);
            TextView is_vegan = findViewById(R.id.s_is_vegan);
            TextView is_gluten_free = findViewById(R.id.s_is_gluten_free);
            TextView is_dairy_free = findViewById(R.id.s_is_dairy_free);
            TextView preparation_time = findViewById(R.id.s_preparation_time);
            TextView ingredient_details = findViewById(R.id.s_ingredients_details);
            TextView preparation_details = findViewById(R.id.s_preparation_details);

            this.recipe_title = bundle.getString("TITLE");
            title.setText(this.recipe_title);

            recipe_id = bundle.getInt("ID");

            if(getIntent().hasExtra("IMAGE")) {
                byte[] image_in_bytes = bundle.getByteArray("IMAGE");
                this.image_src = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);

                img_src.setImageBitmap(this.image_src);
            }
            if(bundle.getBoolean("VEGAN")) { is_vegan.setText("Vegan: Yes"); }
            else{ is_vegan.setText("Vegan: No"); }

            if(bundle.getBoolean("VEGETARIAN")){ is_vegetarian.setText("Vegetarian: Yes"); }
            else{ is_vegetarian.setText("Vegetarian: No"); }

            if(bundle.getBoolean("GLUTEN")) { is_gluten_free.setText("Gluten Free: Yes"); }
            else{is_gluten_free.setText("Gluten Free: No");}

            if(bundle.getBoolean("DAIRY")) { is_dairy_free.setText("Dairy Free: Yes"); }
            else{ is_dairy_free.setText("Dairy Free: No"); }
//            Log.d("ingredients", bundle.getString("INGREDIENTS"));

            this.recipe_ingredients = bundle.getString("INGREDIENTS");
            this.recipe_preparation_steps = bundle.getString("PREPARATION");

            ingredient_details.setText(this.recipe_ingredients);
            preparation_details.setText(this.recipe_preparation_steps);
            Log.d("preparation", bundle.getString("PREPARATION"));
            preparation_time.setText("Ready in " + String.valueOf(bundle.getInt("PREP_TIME")) + "min");
        }
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();

        Paint image = new Paint();
        Paint title = new Paint();
        Paint ingredients = new Paint();
        Paint preparation_steps = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNum).create();

        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(25);
        title.setTextAlign(Paint.Align.CENTER);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        canvas.drawText("Random Recipe Generator", 400, 20, title);
        canvas.drawText("Recipe for " + this.recipe_title, 400, 60, title);

        image.setTextAlign(Paint.Align.CENTER);
        canvas.drawBitmap(scaled_image_src, 300, 100, image);

        ingredients.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        ingredients.setTextSize(25);
        ingredients.setColor(ContextCompat.getColor(this, R.color.purple_500));

        canvas.drawText("Ingredients", 20, 320, ingredients);
        ingredients.setTextSize(20);

        int printHeight = 350;
        int i = 0;

        String regex = "(?<=\\d{1,2}\\.\\s)(.*?)(?=\\.\\n\\d+\\.\\s)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.recipe_ingredients);
        String ingredients_list = "";

        int end = 0;
        while(matcher.find()){
            ingredients_list += ", " + matcher.group();
            end = matcher.end();
        }
        ingredients_list += ", " + (this.recipe_ingredients.substring(end+4,this.recipe_ingredients.length()));

        int startPoint=0,endPoint = 0;
        ingredients_list = ingredients_list.substring(2,ingredients_list.length()-1);
        while(startPoint<ingredients_list.length()) {
            endPoint = startPoint + 78;
            if (endPoint > ingredients_list.length()) {
                endPoint = ingredients_list.length();
            }
            canvas.drawText(ingredients_list.substring(startPoint, endPoint),
                    40, printHeight, ingredients);
            startPoint=endPoint;
            printHeight+=30;
        }

        preparation_steps.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        preparation_steps.setTextSize(25);
        preparation_steps.setColor(ContextCompat.getColor(this, R.color.purple_700));

        canvas.drawText("Steps:", 20, printHeight+10, preparation_steps);
        preparation_steps.setTextSize(20);
        printHeight+=50;

        regex = "(?<=\\d{1,2}\\.\\s)(.*?)(?=\\n\\d+\\.\\s)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(this.recipe_preparation_steps);
        List<String> preparation_step_list = new ArrayList<String>();

        while(matcher.find()){
            preparation_step_list.add(matcher.group());
            end = matcher.end();
        }
        preparation_step_list.add(this.recipe_preparation_steps.substring(end+4,this.recipe_preparation_steps.length()));

        for(i = 0; i<preparation_step_list.size();i++){
            if(printHeight>pageHeight){
                pdfDocument.finishPage(myPage);
                pageNum++;
                mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNum).create();
                myPage = pdfDocument.startPage(mypageInfo);
                canvas = myPage.getCanvas();
                printHeight = 40;
            }
            int startPoint1=0, endPoint1 = 0;
            canvas.drawText(String.valueOf(i+1) + ". ",
                    20, printHeight, preparation_steps);
            while(startPoint1<preparation_step_list.get(i).length()) {
                endPoint1 = startPoint1 + 78;
                if (endPoint1 > preparation_step_list.get(i).length()) {
                    endPoint1 = preparation_step_list.get(i).length();
                }
                canvas.drawText(preparation_step_list.get(i).substring(startPoint1, endPoint1),
                55, printHeight, preparation_steps);
                startPoint1=endPoint1;
                printHeight+=30;
            }
        }

        pdfDocument.finishPage(myPage);

        String directory_path = getApplicationContext().getFilesDir().getPath() + "/Recipes/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+this.recipe_title+".pdf";
        File filePath = new File(targetPdf);
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            Log.d("PDF", "generatePDF: success");
            Toast.makeText(SavedRecipeDetailsActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }


    public void downloadRecipe(View v){
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        scaled_image_src = Bitmap.createScaledBitmap(image_src, 200, 200, false);
        generatePDF();
        Toast.makeText(SavedRecipeDetailsActivity.this, "Recipe downloaded.", Toast.LENGTH_LONG).show();
    }

    public void deleteRecipe(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SavedRecipeDetailsActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this saved recipe?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceAppUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        if (deviceAppUID==null) {
                            Toast.makeText(SavedRecipeDetailsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                        rootNode = FirebaseDatabase.getInstance("https://randomrecipegenerator-default-rtdb.asia-southeast1.firebasedatabase.app");
                        reference = rootNode.getReference("recipes").child(deviceAppUID);
                        reference.child(String.valueOf(recipe_id)).removeValue();
                        Toast.makeText(SavedRecipeDetailsActivity.this, "Recipe deleted.", Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                            }
                        }, 2000);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SavedRecipeDetailsActivity.this, "Phew! You almost lost the recipe!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

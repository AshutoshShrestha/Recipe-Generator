package com.example.randomrecipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.recipefinder.MESSAGE";
    String checkBoxData = "";
    public MainActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBoxData = "";
        TextView subHeader = findViewById(R.id.sub_header_text);
        subHeader.setText("Wanna try something new?\nWe'll help you find what you should try out today!");

        CheckBox vegetarian = ( CheckBox ) findViewById( R.id.vegetarian);
        CheckBox vegan = ( CheckBox ) findViewById( R.id.vegan);
        CheckBox gluten_free = ( CheckBox ) findViewById( R.id.salad);
        CheckBox ketogenic = ( CheckBox ) findViewById( R.id.ketogenic);
        CheckBox chinese = ( CheckBox ) findViewById( R.id.chinese);
        CheckBox indian = ( CheckBox ) findViewById( R.id.indian);
        CheckBox italian = ( CheckBox ) findViewById( R.id.italian);
        CheckBox thai = ( CheckBox ) findViewById( R.id.french);
        CheckBox breakfast = ( CheckBox ) findViewById( R.id.breakfast);
        CheckBox main_course = ( CheckBox ) findViewById( R.id.snack);
        CheckBox side_dish = ( CheckBox ) findViewById( R.id.pescatarian);
        CheckBox dessert = ( CheckBox ) findViewById( R.id.dessert);

        vegetarian.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="vegetarian,";}
            else{checkBoxData=checkBoxData.replaceAll("vegetarian,","");}
        });
        vegan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="vegan,";}
            else{checkBoxData=checkBoxData.replaceAll("vegan,","");}
        });
        gluten_free.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="salad,";}
            else{checkBoxData=checkBoxData.replaceAll("salad,","");}
        });
        ketogenic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="ketogenic,";}
            else{checkBoxData=checkBoxData.replaceAll("ketogenic,","");}
        });
        chinese.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="chinese,";}
            else{checkBoxData=checkBoxData.replaceAll("chinese,","");}
        });
        indian.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="indian,";}
            else{checkBoxData=checkBoxData.replaceAll("indian,","");}
        });
        thai.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="french,";}
            else{checkBoxData=checkBoxData.replaceAll("french,","");}
        });
        italian.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="italian,";}
            else{checkBoxData=checkBoxData.replaceAll("italian,","");}
        });
        breakfast.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="breakfast,";}
            else{checkBoxData=checkBoxData.replaceAll("breakfast,","");}
        });
        main_course.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="snack,";}
            else{checkBoxData=checkBoxData.replaceAll("snack,","");}
        });
        side_dish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="pescatarian,";}
            else{checkBoxData=checkBoxData.replaceAll("pescatarian,","");}
        });
        dessert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked ) { checkBoxData +="dessert,";}
            else{checkBoxData=checkBoxData.replaceAll("dessert,","");}
        });
    }

    public void searchRecipe(View v) {

        EditText searchText = findViewById(R.id.search_text);

        String search_param = checkBoxData + searchText.getText().toString();
        Intent intent = new Intent(this, SearchedRecipeActivity.class);

        intent.putExtra(EXTRA_MESSAGE, search_param);
        startActivity(intent);
    }

    public void savedRecipes(View v) {
        Intent intent = new Intent(this, SavedRecipeActivity.class);
        startActivity(intent);
    }
}
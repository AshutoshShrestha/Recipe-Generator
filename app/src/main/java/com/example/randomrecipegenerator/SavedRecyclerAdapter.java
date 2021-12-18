package com.example.randomrecipegenerator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class SavedRecyclerAdapter extends RecyclerView.Adapter<SavedRecyclerAdapter.MyViewHolder>{
    List<String> recipe_name;
    List<String> img_url;
    List<Integer> preparation_time;
    Bitmap[] img_bitmap = new Bitmap[10];

    Context context;
    JSONArray recipes_data;

    ActivityResultLauncher<Intent> launcher;

    public SavedRecyclerAdapter(Context contxt,
                                List<String> recipe_name,
                                List<String> img_url,
                                List<Integer> prep_time,
                                JSONArray recipes_data,
                                ActivityResultLauncher<Intent> launcher){
        this.context = contxt;
        this.recipe_name = recipe_name;
        this.img_url = img_url;
        this.preparation_time = prep_time;
        this.recipes_data = recipes_data;
        this.launcher = launcher;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, prep_time;
        ImageView recipe_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.saved_title);
            recipe_icon = itemView.findViewById(R.id.saved_recipe_icon);
            prep_time = itemView.findViewById(R.id.saved_prep_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Bundle bundle = new Bundle();

            //passing image bitmap so the app wont have to make fetch request again

            if(img_bitmap[position]!=null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img_bitmap[position].compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bundle.putByteArray("IMAGE", byteArray);
            }
            bundle.putString("TITLE", recipe_name.get(position));
            try {
                bundle.putInt("ID", recipes_data.getJSONObject(position).getInt("id"));
                bundle.putBoolean("VEGAN", recipes_data.getJSONObject(position).getBoolean("is_vegan"));
                bundle.putBoolean("VEGETARIAN", recipes_data.getJSONObject(position).getBoolean("is_vegetarian"));
                bundle.putBoolean("GLUTEN", recipes_data.getJSONObject(position).getBoolean("is_gluten_free"));
                bundle.putBoolean("DAIRY", recipes_data.getJSONObject(position).getBoolean("is_dairy_free"));
                bundle.putString("INGREDIENTS", recipes_data.getJSONObject(position).getString("ingredients"));
                bundle.putString("PREPARATION", recipes_data.getJSONObject(position).getString("preparation"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bundle.putInt("PREP_TIME", preparation_time.get(position));

            Intent intent = new Intent(context, SavedRecipeDetailsActivity.class);
            intent.putExtras(bundle);
            launcher.launch(intent);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.saved_recipe_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecyclerAdapter.MyViewHolder holder, int position) {
        holder.title.setText(recipe_name.get(position));
        fetchImage(holder);
        holder.prep_time.setText("Preparation time: " + String.valueOf(preparation_time.get(position)) + "min");
    }

    @Override
    public int getItemCount() {
        if(recipes_data.length()==0)return 0;
        return recipes_data.length();
    }

    public void fetchImage(@NonNull SavedRecyclerAdapter.MyViewHolder holder) {
        int position = holder.getAdapterPosition();
        img_bitmap[position] = null;
        if (img_url != null && img_url.get(position)!="NA") {
            Picasso.get()
                    .load(img_url.get(position))
                    .placeholder(R.drawable.ic_loading_image)
                    .error(R.drawable.ic_no_image)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.recipe_icon.setImageBitmap(bitmap);
                            img_bitmap[position] = bitmap;
                            Log.d("ImageLoaded", bitmap.toString());
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.d("Bitmap", e.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }
    }}

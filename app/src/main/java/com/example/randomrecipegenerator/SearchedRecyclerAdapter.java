package com.example.randomrecipegenerator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class SearchedRecyclerAdapter extends RecyclerView.Adapter<SearchedRecyclerAdapter.MyViewHolder>{
    List<String> recipe_name, img_url;
    Context context;
    JSONObject recipes_data;
    // will map the image bitmap to the url
    Bitmap image_src[] = new Bitmap[10];

    public SearchedRecyclerAdapter(Context contxt, List<String> title, List<String> img_url, JSONObject recipes_data){
        this.context = contxt;
        this.recipe_name = title;
        this.img_url = img_url;
        this.recipes_data = recipes_data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView recipe_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            recipe_icon = itemView.findViewById(R.id.recipe_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            try {
                JSONObject recipe_data = recipes_data.getJSONArray("recipes").getJSONObject(position);
                Intent intent = new Intent(context, SearchedRecipeDetailsActivity.class);

                //passing image bitmap so the app wont have to make fetch request again
                if(image_src[position]!=null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image_src[position].compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("IMAGE", byteArray);
                }
                Log.d("CLICKED", recipe_data.toString());
                intent.putExtra("RECIPE_DATA", recipe_data.toString());
                context.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.searched_recipe_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedRecyclerAdapter.MyViewHolder holder, int position) {
        fetchImage(holder);
        holder.title.setText(recipe_name.get(position));
    }

    @Override
    public int getItemCount() {
        if(recipe_name==null)return 0;
        return recipe_name.size();
    }

    public void fetchImage(@NonNull SearchedRecyclerAdapter.MyViewHolder holder) {
        int position = holder.getAdapterPosition();
        image_src[position] = null;
        if (img_url.get(position) != "NA") {
            Picasso.get()
                    .load(img_url.get(position))
                    .placeholder(R.drawable.ic_loading_image)
                    .error(R.drawable.ic_no_image)
                    .into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.recipe_icon.setImageBitmap(bitmap);
                    image_src[position] = bitmap;
                    Log.d("ImageLoaded", bitmap.toString());
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.d("Bitmap", e.toString());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    holder.recipe_icon.setImageResource(R.drawable.ic_loading_image);
                }
            });
        }
    }
}

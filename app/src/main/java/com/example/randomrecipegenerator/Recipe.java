package com.example.randomrecipegenerator;

public class Recipe {
    String title, ingredient_details, preparation_details, img_url;
    Boolean is_vegetarian, is_vegan, is_gluten_free, is_dairy_free;
    int preparation_time, id;
    public Recipe(int id,
                  String title,
                  String ingredient_details,
                  String preparation_details,
                  String img_url,
                  Boolean is_vegetarian,
                  Boolean is_vegan,
                  Boolean is_gluten_free,
                  Boolean is_dairy_free,
                  int preparation_time){
        this.id = id;
        this.title = title;
        this.ingredient_details = ingredient_details;
        this.preparation_details = preparation_details;
        this.img_url = img_url;
        this.is_vegetarian = is_vegetarian;
        this.is_vegan = is_vegan;
        this.is_gluten_free = is_gluten_free;
        this.is_dairy_free = is_dairy_free;
        this.preparation_time = preparation_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredient_details() {
        return ingredient_details;
    }

    public void setIngredient_details(String ingredient_details) {
        this.ingredient_details = ingredient_details;
    }

    public String getPreparation_details() {
        return preparation_details;
    }

    public void setPreparation_details(String preparation_details) {
        this.preparation_details = preparation_details;
    }

    public Boolean getIs_vegetarian() {
        return is_vegetarian;
    }

    public void setIs_vegetarian(Boolean is_vegetarian) {
        this.is_vegetarian = is_vegetarian;
    }

    public Boolean getIs_vegan() {
        return is_vegan;
    }

    public void setIs_vegan(Boolean is_vegan) {
        this.is_vegan = is_vegan;
    }

    public Boolean getIs_gluten_free() {
        return is_gluten_free;
    }

    public void setIs_gluten_free(Boolean is_gluten_free) {
        this.is_gluten_free = is_gluten_free;
    }

    public Boolean getIs_dairy_free() {
        return is_dairy_free;
    }

    public void setIs_dairy_free(Boolean is_dairy_free) {
        this.is_dairy_free = is_dairy_free;
    }

    public int getPreparation_time() {
        return preparation_time;
    }

    public void setPreparation_time(int preparation_time) {
        this.preparation_time = preparation_time;
    }
}

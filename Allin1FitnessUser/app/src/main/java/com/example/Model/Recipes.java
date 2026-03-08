package com.example.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class Recipes implements Serializable {
    String rid;
    String type;
    String level;
    String details;
    String ingredients;
    String procedure;
    String img;
    String link;
    List<Ingredient> ingredientlist;
    @Exclude
    List<Item> availableitemlist;
    List<Ingredient> unavailableingredientlist;
    @Exclude
    public List<Item> getAvailableitemlist() {
        return availableitemlist;
    }
    @Exclude
    public void setAvailableitemlist(List<Item> availableitemlist) {
        this.availableitemlist = availableitemlist;
    }
    @Exclude
    public List<Ingredient> getUnavailableingredientlist() {
        return unavailableingredientlist;
    }
    @Exclude
    public void setUnavailableingredientlist(List<Ingredient> unavailableingredientlist) {
        this.unavailableingredientlist = unavailableingredientlist;
    }

    @Exclude
    public Double getSuggestedpercent() {
        return suggestedpercent;
    }

    @Exclude
    public void setSuggestedpercent(Double suggestedpercent) {
        this.suggestedpercent = suggestedpercent;
    }

    @Override
    public String toString() {
        return "Recipes{" +
                "rid='" + rid + '\'' +
                ", type='" + type + '\'' +
                ", level='" + level + '\'' +
                ", details='" + details + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", procedure='" + procedure + '\'' +
                ", img='" + img + '\'' +
                ", link='" + link + '\'' +
                ", ingredientlist=" + ingredientlist +
                ", suggestedpercent=" + suggestedpercent +
                ", name='" + name + '\'' +
                '}';
    }

    @Exclude
    Double suggestedpercent;

    public List<Ingredient> getIngredientlist() {
        return ingredientlist;
    }

    public void setIngredientlist(List<Ingredient> ingredientlist) {
        this.ingredientlist = ingredientlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;


    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}


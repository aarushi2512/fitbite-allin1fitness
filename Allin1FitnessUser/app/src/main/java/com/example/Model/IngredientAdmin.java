package com.example.Model;

import java.io.Serializable;

public class IngredientAdmin implements Serializable {
    String id,rid,ingredient,quantity,measurement;

    public IngredientAdmin(String id, String rid, String ingredient, String quantity, String measurement) {
        this.id = id;
        this.rid = rid;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    public IngredientAdmin(String id, String ingredient, String quantity, String measurement) {
        this.id = id;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    public IngredientAdmin(String ingredient, String quantity, String measurement) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    public IngredientAdmin() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }
}

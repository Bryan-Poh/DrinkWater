package com.bryanpoh.drinkwater;

public class UserData {

    String id, bottleSize, drinkSize, weight, progress, date;

    public UserData(){
        // Empty constructor needed
    }

    public UserData(String id, String bottleSize, String drinkSize, String weight, String progress, String date) {
        this.id = id;
        this.bottleSize = bottleSize;
        this.drinkSize = drinkSize;
        this.weight = weight;
        this.progress = progress;
        this.date = date;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBottleSize() {
        return bottleSize;
    }

    public void setBottleSize(String bottleSize) {
        this.bottleSize = bottleSize;
    }

    public String getDrinkSize() {
        return drinkSize;
    }

    public void setDrinkSize(String drinkSize) {
        this.drinkSize = drinkSize;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

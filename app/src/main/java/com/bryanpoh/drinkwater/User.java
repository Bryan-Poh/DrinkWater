package com.bryanpoh.drinkwater;

public class User {

    String id, username, email, weight, bottleSize, drinkSize;

    public User(){
        // Emtpy constructor needed
    }

    public User(String id, String username, String email, String weight, String bottleSize, String drinkSize) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.weight = weight;
        this.bottleSize = bottleSize;
        this.drinkSize = drinkSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

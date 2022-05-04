package com.jacklee.tuckshopteacher;

import com.google.gson.Gson;

import java.sql.Timestamp;

public class BuyRecord {
    public int RecordId;
    public int StudentId;
    public Timestamp DateTime;
    public Double TotalPrice() {
        return 0.0;
    }
    private String jsonString;

    private Food[] foods;

    public Food[] getFoods() {
        if (foods == null) {
            foods = new Gson().fromJson(jsonString, Food[].class);
        }
        return foods;
    }

    public double getTotalAmount() {
        double sum = 0.00;
        for (Food food : getFoods()) {
            sum += food.Price * food.Quantity;
        }
        return sum;
    }

    public String getJsonString() {
        return  jsonString;
    }
}

package com.example.database.models;

public class Company {

    private int id;
    private float ownWorkingCapital;
    private float ownCapital;
    private float stAssets;
    private float stObligations;
    private float stNetProfit;
    private float assets;
    private float obligation;
    private float revenue;
    private String name;
    private int userId;

    public Company(int id, float ownWorkingCapital, float ownCapital, float stAssets, float stObligations,
                   float stNetProfit, float assets, float obligation, float revenue, String name, int userId) {
        this.id = id;
        this.ownWorkingCapital = ownWorkingCapital;
        this.ownCapital = ownCapital;
        this.stAssets = stAssets;
        this.stObligations = stObligations;
        this.stNetProfit = stNetProfit;
        this.assets = assets;
        this.obligation = obligation;
        this.revenue = revenue;
        this.name = name;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getOwnWorkingCapital() {
        return ownWorkingCapital;
    }

    public void setOwnWorkingCapital(float ownWorkingCapital) {
        this.ownWorkingCapital = ownWorkingCapital;
    }

    public float getOwnCapital() {
        return ownCapital;
    }

    public void setOwnCapital(float ownCapital) {
        this.ownCapital = ownCapital;
    }

    public float getStAssets() {
        return stAssets;
    }

    public void setStAssets(float stAssets) {
        this.stAssets = stAssets;
    }

    public float getStObligations() {
        return stObligations;
    }

    public void setStObligations(float stObligations) {
        this.stObligations = stObligations;
    }

    public float getStNetProfit() {
        return stNetProfit;
    }

    public void setStNetProfit(float stNetProfit) {
        this.stNetProfit = stNetProfit;
    }

    public float getAssets() {
        return assets;
    }

    public void setAssets(float assets) {
        this.assets = assets;
    }

    public float getObligation() {
        return obligation;
    }

    public void setObligation(float obligation) {
        this.obligation = obligation;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

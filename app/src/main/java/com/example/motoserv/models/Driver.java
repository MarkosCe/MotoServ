package com.example.motoserv.models;

public class Driver {

    String id;
    String provider;
    String name;
    String gender;
    String image;
    String brand_vehicle;
    String plate_vehicle;

    public Driver(){

    }

    public Driver(String id, String provider, String name, String gender, String image, String brand_vehicle, String plate_vehicle) {
        this.id = id;
        this.provider = provider;
        this.name = name;
        this.gender = gender;
        this.image = image;
        this.brand_vehicle = brand_vehicle;
        this.plate_vehicle = plate_vehicle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand_vehicle() {
        return brand_vehicle;
    }

    public void setBrand_vehicle(String brand_vehicle) {
        this.brand_vehicle = brand_vehicle;
    }

    public String getPlate_vehicle() {
        return plate_vehicle;
    }

    public void setPlate_vehicle(String plate_vehicle) {
        this.plate_vehicle = plate_vehicle;
    }
}

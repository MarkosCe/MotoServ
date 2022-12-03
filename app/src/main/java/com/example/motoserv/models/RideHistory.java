package com.example.motoserv.models;

public class RideHistory {

    private String idRideHistory;
    private String idClient;
    private String idDriver;
    private String destination;
    private String origin;
    private String time;
    private String km;
    private String status;
    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;
    private double rateClient;
    private double rateDriver;
    private long timestamp;

    public RideHistory(){

    }

    public RideHistory(String idRideHistory, String idClient, String idDriver, String destination, String origin, String time, String km, String status, double originLat, double originLng, double destinationLat, double destinationLng) {
        this.idRideHistory = idRideHistory;
        this.idClient = idClient;
        this.idDriver = idDriver;
        this.destination = destination;
        this.origin = origin;
        this.time = time;
        this.km = km;
        this.status = status;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
    }

    public String getIdRideHistory() {
        return idRideHistory;
    }

    public void setIdRideHistory(String idRideHistory) {
        this.idRideHistory = idRideHistory;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getRateClient() {
        return rateClient;
    }

    public void setRateClient(double rateClient) {
        this.rateClient = rateClient;
    }

    public double getRateDriver() {
        return rateDriver;
    }

    public void setRateDriver(double rateDriver) {
        this.rateDriver = rateDriver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

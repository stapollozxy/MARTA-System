package edu.gatech.Marta;

import java.util.HashMap;

public class Stop {
    private Integer ID;
    private String stopName;
    private Double xCoord;
    private Double yCoord;
    private HashMap<Integer, RiderForRoute> ridersAtStop;
    private HashMap<Integer, Integer> vehicleArrivalTimes;

    public Stop() {
        this.ID = -1;
    }

    public Stop(int uniqueValue) {
        this.ID = uniqueValue;
        this.stopName = "";
        this.xCoord = 0.0;
        this.yCoord = 0.0;
        ridersAtStop = new HashMap<>();
    }

    public Stop(int uniqueValue, String inputName, double inputXCoord, double inputYCoord) {
        this.ID = uniqueValue;
        this.stopName = inputName;
        this.xCoord = inputXCoord;
        this.yCoord = inputYCoord;
        ridersAtStop = new HashMap<>();
        vehicleArrivalTimes = new HashMap<>();
    }

    public void setName(String inputName) { this.stopName = inputName; }

    public void setXCoord(double inputXCoord) { this.xCoord = inputXCoord; }

    public void setYCoord(double inputYCoord) { this.yCoord = inputYCoord; }

    public Integer getID() { return this.ID; }

    public String getName() { return this.stopName; }

    public Double getXCoord() { return this.xCoord; }

    public Double getYCoord() { return this.yCoord; }

    public RiderForRoute getRiderForRoute(int routeID) {
        return ridersAtStop.get(routeID);
    }

    public void setRiderForRoute(int routeID, int timeSlot, int minOns, int avgOns, int maxOns, int minOffs, int avgOffs, int maxOffs) {
        RiderForRoute rider = new RiderForRoute(ID, routeID, 0);
        ridersAtStop.put(routeID, rider);
        rider.addArrivalInfo(timeSlot, minOns, avgOns, maxOns, minOffs, avgOffs, maxOffs);
    }

    public void setVehicleArrivalTime(int vehicleID, int ranking) {
        vehicleArrivalTimes.put(vehicleID, ranking);
    }

    public HashMap<Integer, Integer> getVehicleArrivalTimes() {
        return vehicleArrivalTimes;
    }

    public void displayEvent() {
        System.out.println(" bus stop: " + Integer.toString(this.ID));
    }

/*    public void takeTurn() {
        System.out.println("get new people - exchange with bus when it passes by");
    }

    public Double findDistance(Stop destination) {
        // coordinates are measure in abstract units and conversion factor translates to statute miles
        final double distanceConversion = 70.0;
        return distanceConversion * Math.sqrt(Math.pow((this.xCoord - destination.getXCoord()), 2) + Math.pow((this.yCoord - destination.getYCoord()), 2));
    }*/


    public String displayInternalStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(ID <= 1000000 ? "> Bus Stop" :"> Rail Station");
        sb.append(" - ID: " + Integer.toString(ID));
        sb.append(" name: " + stopName + " waiting: " + Integer.toString(getTotalWaiting()));
        sb.append(" xCoord: " + Double.toString(xCoord) + " yCoord: " + Double.toString(yCoord));
        return sb.toString();
    }

    private int getTotalWaiting() {
        int waiting = 0;
        for (RiderForRoute rider : ridersAtStop.values()) {
            waiting += rider.getWaiting();
        }
        return waiting;
    }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Stop me = (Stop) object;
            if (this.ID == me.getID()) {
                result = true;
            }
        }
        return result;
    }

}
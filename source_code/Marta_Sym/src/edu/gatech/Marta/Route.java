package edu.gatech.Marta;

import java.util.HashMap;

public class Route {
    private Integer ID;
    private Integer routeNumber;
    private String routeName;
    private HashMap<Integer, Integer> stopsOnRoute;
    private boolean isRail;

    private int roundTripTime;

    public Route() {
        this.ID = -1;
    }

    public Route(int uniqueValue) {
        this.ID = uniqueValue;
        this.routeNumber = -1;
        this.routeName = "";
        this.stopsOnRoute = new HashMap<Integer, Integer>();
    }


    public Route(int uniqueValue, int inputNumber, String inputName, boolean isRail) {
        this.ID = uniqueValue;
        this.routeNumber = inputNumber;
        this.routeName = inputName;
        this.stopsOnRoute = new HashMap<Integer, Integer>();
        this.isRail = isRail;
   }

    public void setNumber(int inputNumber) { this.routeNumber = inputNumber; }

    public void setName(String inputName) { this.routeName = inputName; }

    public void addNewStop(int stopID) { this.stopsOnRoute.put(stopsOnRoute.size(), stopID); }

    public Integer getID() { return this.ID; }

    public Integer getNumber() { return this.routeNumber; }

    public String getName() { return this.routeName; }

    public boolean getIsRail() {
        return isRail;
    }

    public int getRoundTripTime() {
        return roundTripTime;
    }

    public void setRoundTripTime(int roundTripTime) {
        this.roundTripTime = roundTripTime;
    }

    public void displayEvent() {
        System.out.println(" bus route: " + Integer.toString(this.ID));
    }

    public void takeTurn() {
        System.out.println("provide next stop on route along with the distance");
    }

    public Integer getNextLocation(int routeLocation) {
        int routeSize = this.stopsOnRoute.size();
        if (routeSize > 0) { return (routeLocation + 1) % routeSize; }
        return -1;
    }

    public Integer getStopID(int routeLocation) { return this.stopsOnRoute.get(routeLocation); }

    public Integer getLength() { return this.stopsOnRoute.size(); }

    public String displayInternalStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(!isRail ? "> Bus Route" :"> Rail Route");
        sb.append(" - ID: " + Integer.toString(ID));
        sb.append(" number: " + Integer.toString(routeNumber) + " name: " + routeName);
        sb.append(" roundTripTime: " + roundTripTime);
        sb.append(" stops: [ ");

        for (int i = 0; i < stopsOnRoute.size(); i++) {
            sb.append(Integer.toString(i) + ":" + Integer.toString(stopsOnRoute.get(i)) + " ");
        }
        sb.append("]");
        return sb.toString();
    }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Route me = (Route) object;
            if (this.ID == me.getID()) {
                result = true;
            }
        }
        return result;
    }
}

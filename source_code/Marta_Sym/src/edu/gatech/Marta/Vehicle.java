package edu.gatech.Marta;

public class Vehicle {
    private Integer ID;
    private Integer route;
    private Integer nextLocation;
    private Integer prevLocation;
    private Integer passengers;
    private Integer capacity;
    //private Integer speed; // given in statute miles per hour

    public Vehicle() {
        this.ID = -1;
    }

    public Vehicle(int uniqueValue) {
        this.ID = uniqueValue;
        this.route = -1;
        this.nextLocation = -1;
        this.prevLocation = -1;
        this.passengers = -1;
        this.capacity = -1;
        //this.speed = -1;
    }

    public Vehicle(int uniqueValue, int inputRoute, int inputLocation, int inputPassengers, int inputCapacity) {
        this.ID = uniqueValue;
        this.route = inputRoute;
        this.nextLocation = inputLocation;
        this.prevLocation = inputLocation;
        this.passengers = inputPassengers;
        this.capacity = inputCapacity;
        //this.speed = inputSpeed;
   }

    public void setRoute(int inputRoute) { this.route = inputRoute; }

    public void setLocation(int inputLocation) {
    	this.prevLocation = this.nextLocation;
    	this.nextLocation = inputLocation;
    }

    public void setPassengers(int inputPassengers) { this.passengers = inputPassengers; }

    public void setCapacity(int inputCapacity) { this.capacity = inputCapacity; }

    //public void setSpeed(int inputSpeed) { this.speed = inputSpeed; }

    public Integer getID() { return this.ID; }

    public Integer getRouteID() { return this.route; }

    public Integer getLocation() { return this.nextLocation; }

    public Integer getPastLocation() { return this.prevLocation; }

    public Integer getPassengers() { return this.passengers; }

    public Integer getCapacity() { return this.capacity; }

    //public Integer getSpeed() { return this.speed; }

    public void displayEvent() {
        System.out.println(" bus: " + Integer.toString(this.ID));
    }

    public String displayInternalStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(route <= 1000 ? "> Bus" :"> Train");
        sb.append(" - ID: " + Integer.toString(ID) + " route: " + Integer.toString(route));
        sb.append(" location from: " + Integer.toString(prevLocation) + " to: " + Integer.toString(nextLocation));
        sb.append(" passengers: " + Integer.toString(passengers) + " capacity: " + Integer.toString(capacity));
        return sb.toString();
    }

    public void takeTurn() {
        System.out.println("drop off passengers - pickup passengers to capacity - move to next stop");
    }

    public void adjustPassengers(int differential) { passengers = passengers + differential; }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Vehicle me = (Vehicle) object;
            if (this.ID == me.getID()) {
                result = true;
            }
        }
        return result;
    }

}

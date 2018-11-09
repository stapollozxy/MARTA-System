package edu.gatech.Marta;
import java.util.HashMap;

public class TransportationSystem {
    private HashMap<Integer, Stop> stops;
    private HashMap<Integer, Route> routes;
    private HashMap<Integer, Vehicle> vehicles;

    public TransportationSystem() {
        stops = new HashMap<Integer, Stop>();
        routes = new HashMap<Integer, Route>();
        vehicles = new HashMap<Integer, Vehicle>();
    }

    public Stop getStop(int stopID) {
        if (stops.containsKey(stopID)) { return stops.get(stopID); }
        return null;
    }

    public Route getRoute(int routeID) {
        if (routes.containsKey(routeID)) { return routes.get(routeID); }
        return null;
    }

    public Vehicle getVehicle(int vehicleID) {
        if (vehicles.containsKey(vehicleID)) { return vehicles.get(vehicleID); }
        return null;
    }

    public int makeStop(int uniqueID, String inputName, double inputXCoord, double inputYCoord) {
        // int uniqueID = stops.size();
        if (uniqueID <= 1000000) {
            stops.put(uniqueID, new BusStop(uniqueID, inputName, inputXCoord, inputYCoord));
        } else {
            stops.put(uniqueID, new RailStation(uniqueID, inputName, inputXCoord, inputYCoord));
        }
        return uniqueID;
    }

    public int makeRoute(int uniqueID, int inputNumber, String inputName) {
        // int uniqueID = routes.size();
        routes.put(uniqueID, new Route(uniqueID, inputNumber, inputName, uniqueID > 1000));
        return uniqueID;
    }

    public int makeVehicle(int uniqueID, int inputRoute, int inputLocation, int inputPassengers, int inputCapacity) {
        // int uniqueID = vehicles.size();
        vehicles.put(uniqueID, new Vehicle(uniqueID, inputRoute, inputLocation, inputPassengers, inputCapacity));
        return uniqueID;
    }

    public void appendStopToRoute(int routeID, int nextStopID) { routes.get(routeID).addNewStop(nextStopID); }

    public HashMap<Integer, Stop> getStops() { return stops; }

    public HashMap<Integer, Route> getRoutes() { return routes; }

    public HashMap<Integer, Vehicle> getVehicles() { return vehicles; }
}

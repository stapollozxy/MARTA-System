package edu.gatech.Marta;

import edu.gatech.DB.DBUtil;

import java.util.*;
import java.sql.*;

public class SimDriver {
    private static SimQueue simEngine;
    private static TransportationSystem martaModel;
    private static Random randGenerator;

    public SimDriver() {
        simEngine = new SimQueue();
        martaModel = new TransportationSystem();
        randGenerator = new Random();
    }

    public String runInterpreter(String userCommandLine) {
        final String DELIMITER = ",";
        StringBuilder sb = new StringBuilder();
        try {

            System.out.print("# main: ");
            //String userCommandLine = takeCommand.nextLine();
          //  String userCommandLine = command[index];
            String[] tokens = userCommandLine.split(DELIMITER);

            switch (tokens[0]) {
                case "add_event":
                    simEngine.addNewEvent(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                    sb.append("new event - rank: " + Integer.parseInt(tokens[1]));
                    sb.append(" type: " + tokens[2] + " ID: " + Integer.parseInt(tokens[3]) + " created");
                    break;
                case "add_stop":
                    int stopID = martaModel.makeStop(Integer.parseInt(tokens[1]), tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
                    sb.append("new stop: " + Integer.toString(stopID) + " created");
                    break;
                case "add_route":
                    int routeID = martaModel.makeRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
                    sb.append("new route: " + Integer.toString(routeID) + " created");
                    break;
                case "add_vehicle":
                    int busID = martaModel.makeVehicle(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
                    sb.append("new bus: " + Integer.toString(busID) + " created");
                    break;
                case "extend_route":
                    routeID = Integer.parseInt(tokens[1]);
                    stopID = Integer.parseInt(tokens[2]);
                    martaModel.appendStopToRoute(routeID, stopID);
                    martaModel.getStop(stopID).setRiderForRoute(routeID,24,0,0,0,0,0,0);
                    calculateRoundTripTime(routeID);
                    sb.append("stop: " + Integer.parseInt(tokens[2]) + " appended to route " + Integer.parseInt(tokens[1]));
                    break;
                case "upload_real_data":
                    //success = uploadMARTAData();
                    String temp = uploadMARTAData();
                    sb.append(temp);
                    break;
                case "step_once":
                    sb.append(" queue activated for 1 event" + "\n");
                    sb.append(simEngine.triggerNextEvent(martaModel));
                    break;
                case "step_multi":
                    sb.append(" queue activated for " + Integer.parseInt(tokens[1]) + " event(s)" + "\n");
                    for (int i = 0; i < Integer.parseInt(tokens[1]); i++) {
                    	// display the number of events completed for a given frequency
                    	if (tokens.length >= 3) {
                    		if (i % Integer.parseInt(tokens[2]) == 0) {
                    		    sb.append("> " + Integer.toString(i) + " events completed" + "\n");
                    		}
                    	}
                    	
                    	// execute the next event
                    	sb.append(simEngine.triggerNextEvent(martaModel));
                    	
                    	// pause after each event for a given number of seconds
                    	if (tokens.length >= 4) {
                    		try { Thread.sleep(Integer.parseInt(tokens[3]) * 1000); }
                    			catch (InterruptedException e) { e.printStackTrace(); }
                    	}
                    }
                    break;
                case "system_report":
                    sb.append(" system report - stops, vehicles and routes:" + "\n");

                    for (Stop singleStop: martaModel.getStops().values()) {
                        StringBuilder stopString = new StringBuilder();

                        String tmp = singleStop.displayInternalStatus();
                        stopString.append(tmp);
                        stopString.append(" nextArrivals: [ ");

                        for (Map.Entry<Integer, Integer> entry : singleStop.getVehicleArrivalTimes().entrySet()) {
                            Vehicle vehicle = martaModel.getVehicle(entry.getKey());
                            Route route = martaModel.getRoute(vehicle.getRouteID());
                            int nextArrivalTime = entry.getValue() + route.getRoundTripTime() - simEngine.getCurrentRanking();
                            stopString.append(entry.getKey() + ":" + nextArrivalTime + " ");
                        }

                        stopString.append("]");
                        //r.addStop(singleStop.getID(), stopString.toString());
                        sb.append(stopString + "\n");
                    }
                    for (Vehicle singleVehicle : martaModel.getVehicles().values()) {
                        String tmp = singleVehicle.displayInternalStatus();
                        sb.append(tmp + "\n");
                        //r.addVehicle(singleVehicle.getID(), tmp);
                    }
                    for (Route singleRoute: martaModel.getRoutes().values()) {
                        String tmp = singleRoute.displayInternalStatus();
                        sb.append(tmp + "\n");
                        //r.addRoute(singleRoute.getID(), tmp);
                    }
                    //success = true;
                    break;
                case "quit":
                    sb.append("stop the command loop");
                    //success = true;
                    break;
                default:
                    sb.append("command not recognized");
                    break;
            }

        } catch (Exception e) {
            sb.append("invalid input");
        }
        System.out.print(sb);
        return sb.toString();
    }

    public static String uploadMARTAData() {
        StringBuilder sb = new StringBuilder();
        ResultSet rs;
        int recordCounter;
        boolean uploadSuccess = false;
        Integer stopID, routeID;
        String stopName, routeName;
        // String direction;
        Double latitude, longitude;

        // intermediate data structures needed for assembling the routes
        HashMap<Integer, ArrayList<Integer>> routeLists = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> targetList;
        ArrayList<Integer> circularRouteList = new ArrayList<Integer>();

        try {
    		// connect to the local database system
            sb.append(" connecting to the database" + "\n");
            Class.forName("org.postgresql.Driver");
    		String url = DBUtil.URL;
    		Properties props = new Properties();
    		props.setProperty("user", DBUtil.USERNAME);
    		props.setProperty("password", DBUtil.PASSWORD);
    		props.setProperty("ssl", "false");

			Connection conn = DriverManager.getConnection(url, props);
			Statement stmt = conn.createStatement();

			// create the stops
        	sb.append(" extracting and adding the stops: ");
        	recordCounter = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_stops");
            while (rs.next()) {
                stopID = rs.getInt("min_stop_id");
                stopName = rs.getString("stop_name");
                latitude = rs.getDouble("latitude");
                longitude = rs.getDouble("longitude");

                martaModel.makeStop(stopID,stopName,latitude,longitude);
                recordCounter++;
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");

            // create the routes
        	sb.append(" extracting and adding the routes: ");
        	recordCounter = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_routes");
            while (rs.next()) {
                routeID = rs.getInt("route");
                routeName = rs.getString("route_name");

                martaModel.makeRoute(routeID, routeID, routeName);
                recordCounter++;

                // initialize the list of stops for the route as needed
                routeLists.putIfAbsent(routeID, new ArrayList<Integer>());
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");

            // add the stops to all of the routes
        	sb.append(" extracting and assigning stops to routes: ");
        	recordCounter = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_routelist_oneway");
            while (rs.next()) {
                routeID = rs.getInt("route");
                stopID = rs.getInt("min_stop_id");
                // direction = rs.getString("direction");

                targetList = routeLists.get(routeID);
                if (!targetList.contains(stopID)) {
                    martaModel.appendStopToRoute(routeID, stopID);
                    recordCounter++;
                    targetList.add(stopID);
                    // if (direction.equals("Clockwise")) { circularRouteList.add(routeID); }
                }
            }
            sb.append(Integer.toString(recordCounter) + " assigned;" + "\n");

            /*// add the reverse "route back home" stops for two-way routes
            for (Integer reverseRouteID : routeLists.keySet()) {
                if (!circularRouteList.contains(reverseRouteID)) {
                    targetList = routeLists.get(reverseRouteID);
                    for (int i = targetList.size() - 2; i > 0; i--) {
                        martaModel.appendStopToRoute(reverseRouteID, targetList.get(i));
                    }
                }
            }*/

            // create the buses and related event(s)
        	sb.append(" extracting and adding vehicles and events: ");
        	recordCounter = 0;
            int busID = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_bus_distributions");
            while (rs.next()) {
                routeID = rs.getInt("route");
                int minBuses = rs.getInt("min_buses");
                int avgBuses  = rs.getInt("avg_buses");
                int maxBuses = rs.getInt("max_buses");

                int routeLength = martaModel.getRoute(routeID).getLength();
                int suggestedBuses = randomBiasedValue(minBuses, avgBuses, maxBuses);
                int busesOnRoute = Math.max(1, Math.min(routeLength / 2, suggestedBuses));

                int startingPosition = 0;
                int skip = Math.max(1, routeLength / busesOnRoute);
                for (int i = 0; i < busesOnRoute; i++) {
                    if (routeID <= 1000) {
                        martaModel.makeVehicle(busID, routeID, startingPosition + i * skip, 0, 10);
                    } else {
                        martaModel.makeVehicle(busID, routeID, startingPosition + i * skip, 0, 100);
                    }
                    simEngine.addNewEvent(0,"move_vehicle", busID++);
                    recordCounter++;
                }
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");

            // create the rider-passenger generator and associated event(s)
        	sb.append(" extracting and adding rider frequency timeslots: ");
        	recordCounter = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_rider_distributions");
            while (rs.next()) {
                stopID = rs.getInt("min_stop_id");
                routeID = rs.getInt("route");
                int timeSlot = rs.getInt("time_slot");
                int minOns = rs.getInt("min_ons");
                int avgOns  = rs.getInt("avg_ons");
                int maxOns = rs.getInt("max_ons");
                int minOffs = rs.getInt("min_offs");
                int avgOffs = rs.getInt("avg_offs");
                int maxOffs = rs.getInt("max_offs");

                martaModel.getStop(stopID).setRiderForRoute(routeID, timeSlot, minOns, avgOns, maxOns, minOffs, avgOffs, maxOffs);
                recordCounter++;
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");


            // create the rider-passenger generator and associated event(s)
            sb.append(" extracting and adding rail travel time: ");
            recordCounter = 0;
            rs = stmt.executeQuery("SELECT * FROM apcdata_travel_time");
            while (rs.next()) {
                int originID = rs.getInt("origin");
                int destinationID = rs.getInt("destination");
                int travelTime = rs.getInt("travel_time")/60;
                ((RailStation)martaModel.getStop(originID)).setTravelTime(destinationID, travelTime);
                //((RailStation)martaModel.getStop(destinationID)).setTravelTime(originID, travelTime);
                recordCounter++;
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");

            // calculate route round trip time
            sb.append(" calculating route round trip time: ");
            recordCounter = 0;
            for (int ID : routeLists.keySet()) {
                calculateRoundTripTime(ID);
                recordCounter++;
            }
            sb.append(Integer.toString(recordCounter) + " added;" + "\n");

        } catch (Exception e) {
            sb.append("Discovered exception: " + e.getMessage() + "\n");
        }
        return sb.toString();
    }

    private static int randomBiasedValue(int lower, int middle, int upper) {
        int lowerRange = randGenerator.nextInt(middle - lower + 1) + lower;
        int upperRange = randGenerator.nextInt(upper - middle + 1) + middle;
        return (lowerRange + upperRange) /2;
    }

    private static void calculateRoundTripTime(int ID) {
        int roundTripTime = 0;
        Route route = martaModel.getRoute(ID);

        for (int i = 0; i < route.getLength(); i++) {
            int next = i == route.getLength() - 1 ? 0 : i + 1;
            if (!route.getIsRail()) {
                roundTripTime += ((BusStop) martaModel.getStop(route.getStopID(i))).getTravelTime(martaModel.getStop(route.getStopID(next)));
            } else {
                roundTripTime += ((RailStation) martaModel.getStop(route.getStopID(i))).getTravelTime(martaModel.getStop(route.getStopID(next)));
            }
        }
        route.setRoundTripTime(roundTripTime);
    }
}

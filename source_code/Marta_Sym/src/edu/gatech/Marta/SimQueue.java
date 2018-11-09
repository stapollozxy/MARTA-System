package edu.gatech.Marta;

import java.util.Comparator;
import java.util.PriorityQueue;

public class SimQueue {
    private static PriorityQueue<SimEvent> eventQueue;
    private Comparator<SimEvent> simComparator;

    public SimQueue() {
        simComparator = new SimEventComparator();
        eventQueue = new PriorityQueue<SimEvent>(100, simComparator);
    }

    public String triggerNextEvent(TransportationSystem busModel) {
        StringBuilder sb = new StringBuilder();
        if (eventQueue.size() > 0) {
            SimEvent activeEvent = eventQueue.poll();
            sb.append(activeEvent.displayEvent());
            switch (activeEvent.getType()) {
                case "move_vehicle":
                    // identify the bus that will move
                    Vehicle activeVehicle = busModel.getVehicle(activeEvent.getID());
                    sb.append(" the vehicle being observed is: " + Integer.toString(activeVehicle.getID())+ "\n");
                    // identify the current stop
                    Route activeRoute = busModel.getRoute(activeVehicle.getRouteID());
                    sb.append(" the vehicle is driving on route: " + Integer.toString(activeRoute.getID())+ "\n");
                    int activeLocation = activeVehicle.getLocation();
                    int activeStopID = activeRoute.getStopID(activeLocation);
                    Stop activeStop = busModel.getStop(activeStopID);
                    sb.append(" the vehicle is currently at stop: " + Integer.toString(activeStop.getID()) + " - " + activeStop.getName()+ "\n");

                    // drop off and pickup new passengers at current stop
                    int currentPassengers = activeVehicle.getPassengers();
                    int passengerDifferential = activeStop.getRiderForRoute(activeRoute.getID()).exchangeRiders(activeEvent.getRank(), currentPassengers, activeVehicle.getCapacity());
                    sb.append(" passengers pre-stop: " + Integer.toString(currentPassengers) + " post-stop: " + (currentPassengers + passengerDifferential)+ "\n");
                    activeVehicle.adjustPassengers(passengerDifferential);

                    // set vehicle arrival time at the stop
                    activeStop.setVehicleArrivalTime(activeEvent.getID(), activeEvent.getRank());

                    // determine next stop
                    int nextLocation = activeRoute.getNextLocation(activeLocation);
                    int nextStopID = activeRoute.getStopID(nextLocation);
                    Stop nextStop = busModel.getStop(nextStopID);
                    sb.append(" the vehicle is heading to stop: " + Integer.toString(nextStopID) + " - " + nextStop.getName() + "\n");
                    // find travel time to stop to determine next event time
                    int travelTime = 0;
                    if (!activeRoute.getIsRail()) {
                        travelTime = ((BusStop) activeStop).getTravelTime(nextStop, activeEvent.getRank());
                    } else {
                        travelTime = ((RailStation)activeStop).getTravelTime(nextStop);
                    }

                    activeVehicle.setLocation(nextLocation);

                    // generate next event for this bus
                    eventQueue.add(new SimEvent(activeEvent.getRank() + travelTime, "move_vehicle", activeEvent.getID()));
                    break;
                default:
                    sb.append(" event not recognized" + "\n");
                    break;
            }
        } else {
            sb.append(" event queue empty" + "\n");
        }
        System.out.print(sb);
        return sb.toString();
    }

    public void addNewEvent(Integer eventRank, String eventType, Integer eventID) {
        eventQueue.add(new SimEvent(eventRank, eventType, eventID));
    }

    public int getCurrentRanking() {
        return eventQueue.peek().getRank();
    }
}

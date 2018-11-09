package edu.gatech.Marta;

import java.util.HashMap;
import java.util.Random;

public class RiderForRoute {

    private int stopID;
    private int routeID;
    private Random randGenerator;
    private HashMap<Integer, int[]> rateCatchingBus;
    private HashMap<Integer, int[]> rateLeavingBus;
    private Integer waiting;

    public RiderForRoute() {
        this.stopID = -1;
        this.routeID = -1;
    }

    public RiderForRoute(int stopID, int routeID, int incomingRiders) {
        this.stopID = stopID;
        this.routeID = routeID;
        this.randGenerator = new Random();
        this.rateCatchingBus = new HashMap<Integer, int[]>();
        this.rateLeavingBus = new HashMap<Integer, int[]>();
        this.waiting = incomingRiders;
    }

    public int getWaiting() {
        return this.waiting;
    }


    public Integer exchangeRiders(int rank, int initialPassengerCount, int capacity) {
        int hourOfTheDay = (rank / 60) % 24;
        int ableToBoard;
        int[] leavingBusRates, catchingBusRates;
        int[] filler = new int[]{0, 1, 1};

        // calculate expected number riders leaving the bus
        if (rateLeavingBus.containsKey(hourOfTheDay)) { leavingBusRates = rateLeavingBus.get(hourOfTheDay); }
        else { leavingBusRates = filler; }
        int leavingBus = randomBiasedValue(leavingBusRates[0], leavingBusRates[1], leavingBusRates[2]);

        // update the number of riders actually leaving the bus versus the current number of passengers
        int updatedPassengerCount = Math.max(0, initialPassengerCount - leavingBus);

        // calculate expected number riders leaving the bus
        if (rateCatchingBus.containsKey(hourOfTheDay)) { catchingBusRates = rateCatchingBus.get(hourOfTheDay); }
        else { catchingBusRates = filler; }
        int catchingBus = randomBiasedValue(catchingBusRates[0], catchingBusRates[1], catchingBusRates[2]);

        // determine how many of the currently waiting and new passengers will fit on the bus
        int tryingToBoard = waiting + catchingBus;
        int availableSeats = capacity - updatedPassengerCount;

        // update the number of passengers left waiting for the next bus
        if (tryingToBoard > availableSeats) {
            ableToBoard = availableSeats;
            waiting = tryingToBoard - availableSeats;
        } else {
            ableToBoard = tryingToBoard;
            waiting = 0;
        }

        // update the number of riders actually catching the bus and return the difference from the original riders
        int finalPassengerCount = updatedPassengerCount + ableToBoard;
        return finalPassengerCount - initialPassengerCount;
    }

    public void addArrivalInfo(int timeSlot, int minOn, int avgOn, int maxOn, int minOff, int avgOff, int maxOff) {
        rateCatchingBus.put(timeSlot, new int[]{minOn, avgOn, maxOn});
        rateLeavingBus.put(timeSlot, new int[]{minOff, avgOff, maxOff});
    }

    private int randomBiasedValue(int lower, int middle, int upper) {
        int lowerRange = randGenerator.nextInt(middle - lower + 1) + lower;
        int upperRange = randGenerator.nextInt(upper - middle + 1) + middle;
        return (lowerRange + upperRange) /2;
    }


}

package edu.gatech.Marta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RailStation extends Stop{
    private HashMap<Integer, Integer> travelTimes;

    public RailStation(int uniqueValue, String inputName, double inputXCoord, double inputYCoord) {
        super(uniqueValue, inputName, inputXCoord, inputYCoord);
        travelTimes = new HashMap<>();

    }

    public void setTravelTime(int destinationID, int travelTime) {
        travelTimes.put(destinationID, travelTime);
    }

    public int getTravelTime(Stop destination) {
        if (travelTimes.get(destination.getID()) != null) {
            return travelTimes.get(destination.getID());
        } else {
            return queryGoogleMap(getXCoord(), getYCoord(), destination.getXCoord(), destination.getYCoord());
        }
    }

    private static final String API_KEY = "AIzaSyBtJZaK6DAylZ3z0qgFnkhbBK_tVb_EPPs";
    private int queryGoogleMap(double oLat, double oLong, double dLat, double dLong) {

        String query = String.format("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=%s" +
                ",%s&destinations=%s,%s&key=" + API_KEY, oLat, oLong, dLat, dLong);
        try {
            // Open a HTTP connection between your Java application and TicketMaster based on url
            HttpURLConnection connection = (HttpURLConnection) new URL(query).openConnection();
            // Set requrest method to GET
            connection.setRequestMethod("GET");

            // Send request to TicketMaster and get response, response code could be returned directly
            // response body is saved in InputStream of connection.
            int responseCode = connection.getResponseCode();
            //System.out.println("\nSending 'GET' request to URL : " + query);
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject responseJson = new JSONObject(response.toString());
            JSONArray rows = (JSONArray) responseJson.getJSONArray("rows");
            JSONObject rows1 = (JSONObject) rows.get(0);
            JSONArray elements = (JSONArray) rows1.getJSONArray("elements");
            JSONObject e = (JSONObject) elements.get(0);

            JSONObject dit = (JSONObject) e.getJSONObject("duration");
            return dit.getInt("value") / 60;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

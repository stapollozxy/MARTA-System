package edu.gatech.Marta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusStop extends Stop{
    private static final String API_KEY = "AIzaSyBtJZaK6DAylZ3z0qgFnkhbBK_tVb_EPPs";
    private static final Integer SIMULATION_START_TIME = 1525147200; // 05/01/2018 12:00:00am ETC

    public BusStop(int uniqueValue, String inputName, double inputXCoord, double inputYCoord) {
        super(uniqueValue, inputName, inputXCoord, inputYCoord);
    }

    // return travel time in minutes
    public int getTravelTime(Stop destination) {
        return queryGoogleMap(getXCoord(), getYCoord(), destination.getXCoord(), destination.getYCoord());
    }

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

    // return travel time in minutes with traffic condition
    public int getTravelTime(Stop destination, int ranking) {
        return queryGoogleMap(getXCoord(), getYCoord(), destination.getXCoord(), destination.getYCoord(), SIMULATION_START_TIME + ranking * 60);
    }

    private int queryGoogleMap(double oLat, double oLong, double dLat, double dLong, int dtime) {

        String query = String.format("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=%s" +
                ",%s&destinations=%s,%s&departure_time=%s&key=" + API_KEY, oLat, oLong, dLat, dLong, dtime);
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

            JSONObject dit = (JSONObject) e.getJSONObject("duration_in_traffic");
            return dit.getInt("value")/60;

        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

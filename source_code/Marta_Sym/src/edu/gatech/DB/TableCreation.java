package edu.gatech.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class TableCreation {
    // Run this as Java application to reset db schema.
    public static void main(String[] args) {
        try {
            // Ensure the driver is imported.
            Class.forName("org.postgresql.Driver").newInstance();
            // This is java.sql.Connection.
            Connection conn = null;

            // Step 1 Connect to PostgreSQL.
            try {
                System.out.println("Connecting to \n" + DBUtil.URL);
                conn = DriverManager.getConnection(DBUtil.URL, DBUtil.USERNAME, DBUtil.PASSWORD);
            } catch (SQLException e) {
                System.out.println("SQLException " + e.getMessage());
                System.out.println("SQLState " + e.getSQLState());
                System.out.println("VendorError " + e.getErrorCode());
            }

            if (conn == null) {
                return;
            }
            // Step 2 Drop tables in case they exist.
            Statement stmt = conn.createStatement();

            String sql = "DROP TABLE IF EXISTS apcdata_source1 cascade";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS apcdata cascade";
            stmt.executeUpdate(sql);

            // Step 3. Create new tables.

            sql = "CREATE TABLE apcdata (" +
                    " calendar_day date," +
                    " route smallint," +
                    " route_name character varying(125)," +
                    " direction character varying(12)," +
                    " stop_id integer," +
                    " stop_name character varying(125)," +
                    " arrival_time time without time zone," +
                    " departure_time time without time zone," +
                    " ons smallint," +
                    " offs smallint," +
                    " latitude numeric," +
                    " longitude numeric," +
                    " vehicle_number integer" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("Creation is done successfully.");
            // Step 4: insert data


            sql = "COPY apcdata FROM \'" + DBUtil.Path + args[0] + "\' DELIMITER "
                    + DBUtil.DELIMITER + " CSV HEADER";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            System.out.println("Import is done successfully.");


            // step 5: create view for "upload_real_data" command
            // 1 Name: apcdata_activity_only; Type: VIEW; Schema: public;
            sql = "CREATE VIEW apcdata_activity_only AS" +
                    " SELECT apcdata.calendar_day," +
                    " apcdata.route," +
                    " apcdata.route_name," +
                    " apcdata.direction," +
                    " apcdata.stop_id," +
                    " apcdata.stop_name," +
                    " apcdata.arrival_time," +
                    " apcdata.departure_time," +
                    " apcdata.ons," +
                    " apcdata.offs," +
                    " apcdata.latitude," +
                    " apcdata.longitude," +
                    " apcdata.vehicle_number" +
                    " FROM apcdata" +
                    " WHERE (((apcdata.ons > 0) OR (apcdata.offs > 0)) AND " +
                    "(apcdata.latitude IS NOT NULL) AND (apcdata.longitude IS NOT NULL) AND " +
                    "((apcdata.arrival_time IS NOT NULL) OR (apcdata.departure_time IS NOT NULL)))";
            stmt.executeUpdate(sql);
            System.out.println("1 is done successfully.");
            // 2 Name: unique_stopid_per_name; Type: VIEW; Schema: public;
            sql = "CREATE VIEW unique_stopid_per_name AS" +
                    " SELECT min(apcdata_activity_only.stop_id) AS min_stop_id," +
                    " apcdata_activity_only.stop_name AS min_stop_name" +
                    " FROM apcdata_activity_only" +
                    " GROUP BY apcdata_activity_only.stop_name";
            stmt.executeUpdate(sql);
            System.out.println("2 is done successfully.");

            // 3 Name: apcdata_source; Type: VIEW; Schema: public
            sql = "CREATE VIEW apcdata_source AS\n" +
                    " SELECT apcdata_activity_only.calendar_day," +
                    " apcdata_activity_only.route," +
                    " apcdata_activity_only.route_name," +
                    " apcdata_activity_only.direction," +
                    " unique_stopid_per_name.min_stop_id," +
                    " apcdata_activity_only.stop_name," +
                    " apcdata_activity_only.arrival_time," +
                    " apcdata_activity_only.departure_time," +
                    " apcdata_activity_only.ons," +
                    " apcdata_activity_only.offs," +
                    " apcdata_activity_only.latitude," +
                    " apcdata_activity_only.longitude," +
                    " apcdata_activity_only.vehicle_number" +
                    " FROM apcdata_activity_only," +
                    " unique_stopid_per_name" +
                    " WHERE ((apcdata_activity_only.stop_name)::text = (unique_stopid_per_name.min_stop_name)::text)" +
                    " ORDER BY apcdata_activity_only.vehicle_number, apcdata_activity_only.calendar_day, " +
                    "apcdata_activity_only.arrival_time, apcdata_activity_only.departure_time";
            stmt.executeUpdate(sql);
            System.out.println("3 is done successfully.");

            // 10 Name: scource 1
            sql = "SELECT *, row_number() OVER ()" +
                    " INTO apcdata_source1" +
                    " FROM apcdata_source";
            stmt.executeUpdate(sql);
            System.out.println("10 is done successfully.");

            // 4 Name: apcdata_bus_distributions; Type: VIEW; Schema: public;
            sql = "CREATE VIEW apcdata_bus_distributions AS" +
                    " SELECT temp.route," +
                    " min(temp.bus_count) AS min_buses," +
                    " trunc(avg(temp.bus_count)) AS avg_buses," +
                    " max(temp.bus_count) AS max_buses" +
                    " FROM ( SELECT apcdata_source.calendar_day," +
                    " apcdata_source.route," +
                    " count(DISTINCT apcdata_source.vehicle_number) AS bus_count" +
                    " FROM apcdata_source" +
                    " GROUP BY apcdata_source.calendar_day, apcdata_source.route) temp" +
                    " GROUP BY temp.route";
            stmt.executeUpdate(sql);
            System.out.println("4 is done successfully.");

            // 5 Name: apcdata_rider_distributions; Type: VIEW; Schema: public
            sql = "CREATE VIEW apcdata_rider_distributions AS" +
                    " SELECT temp.min_stop_id," +
                    " temp.route," +
                    " temp.time_slot," +
                    " min(temp.on_sum) AS min_ons," +
                    " trunc(avg(temp.on_sum)) AS avg_ons," +
                    " max(temp.on_sum) AS max_ons," +
                    " min(temp.off_sum) AS min_offs," +
                    " trunc(avg(temp.off_sum)) AS avg_offs," +
                    " max(temp.off_sum) AS max_offs" +
                    " FROM ( SELECT apcdata_source.calendar_day," +
                    " apcdata_source.min_stop_id," +
                    " apcdata_source.route," +
                    " date_part('hour'::text, apcdata_source.arrival_time) AS time_slot," +
                    " sum(apcdata_source.ons) AS on_sum," +
                    " sum(apcdata_source.offs) AS off_sum" +
                    " FROM apcdata_source" +
                    " GROUP BY apcdata_source.calendar_day, apcdata_source.min_stop_id, apcdata_source.route, (date_part('hour'::text, apcdata_source.arrival_time))) temp" +
                    " WHERE (temp.time_slot IS NOT NULL)" +
                    " GROUP BY temp.min_stop_id, temp.route, temp.time_slot";
            stmt.executeUpdate(sql);
            System.out.println("5 is done successfully.");

            // 6 Name: apcdata_routelist_oneway; Type: VIEW; Schema: public
            sql = "CREATE VIEW apcdata_routelist_oneway AS" +
                    " SELECT apcdata_source1.route, unique_stopid_per_name.min_stop_id, apcdata_source1.row_number" +
                    " FROM apcdata_source1, unique_stopid_per_name" +
                    " WHERE (apcdata_source1.stop_name = unique_stopid_per_name.min_stop_name) " +
                    //" (apcdata_source1.direction = ANY (ARRAY[('Northbound'::character varying)::text, " +
                    //" ('Eastbound'::character varying)::text, ('Clockwise'::character varying)::text]))" +
                    " ORDER BY apcdata_source1.row_number";
            stmt.executeUpdate(sql);
            System.out.println("6 is done successfully.");

            // 7 Name: apcdata_routes; Type: VIEW; Schema: public
            sql = "CREATE VIEW apcdata_routes AS" +
                    " SELECT DISTINCT apcdata_source.route," +
                    " apcdata_source.route_name" +
                    " FROM apcdata_source";
            stmt.executeUpdate(sql);
            System.out.println("7 is done successfully.");

            // 8 Name: apcdata_stops; Type: VIEW; Schema: public
            sql = "CREATE VIEW apcdata_stops AS" +
                    " SELECT apcdata_source.min_stop_id," +
                    " apcdata_source.stop_name," +
                    " (avg(apcdata_source.latitude)) AS latitude," +
                    " (avg(apcdata_source.longitude)) AS longitude" +
                    " FROM apcdata_source" +
                    " GROUP BY apcdata_source.min_stop_id, apcdata_source.stop_name";
            stmt.executeUpdate(sql);
            System.out.println("8 is done successfully.");

            // 9 Name: location_checks; Type: VIEW; Schema: public
            sql = "CREATE VIEW location_checks AS" +
                    " SELECT temp.min_stop_id," +
                    " temp.stop_name," +
                    " (((temp.max_lat - temp.min_lat) ^ (2)::numeric) + ((temp.max_long - temp.min_long) ^ (2)::numeric)) AS distance_diff" +
                    " FROM ( SELECT apcdata_source.min_stop_id," +
                    " apcdata_source.stop_name," +
                    " max(apcdata_source.latitude) AS max_lat," +
                    " min(apcdata_source.latitude) AS min_lat," +
                    " max(apcdata_source.longitude) AS max_long," +
                    " min(apcdata_source.longitude) AS min_long" +
                    " FROM apcdata_source" +
                    " GROUP BY apcdata_source.min_stop_id, apcdata_source.stop_name) temp" +
                    " ORDER BY (((temp.max_lat - temp.min_lat) ^ (2)::numeric) + ((temp.max_long - temp.min_long) ^ (2)::numeric)) DESC";
            stmt.executeUpdate(sql);
            System.out.println("9 is done successfully.");

            // 11 Name: apc traval time
            sql = "CREATE VIEW apcdata_travel_time AS" +
                    "  SELECT apcdata_source1.min_stop_id AS origin, TEMP.min_stop_id as destination, avg(EXTRACT(HOUR FROM " +
                    "(TEMP.arrival_time - apcdata_source1.arrival_time))*3600 + EXTRACT(MINUTE FROM " +
                    "(TEMP.arrival_time - apcdata_source1.arrival_time))*60 + EXTRACT(SECOND from " +
                    "(TEMP.arrival_time - apcdata_source1.arrival_time))) AS travel_time" +
                    " FROM apcdata_source1 INNER JOIN apcdata_source1 AS TEMP ON (apcdata_source1.row_number + 1 = TEMP.row_number)" +
                    " WHERE (apcdata_source1.route > 1000) AND TEMP.arrival_time > apcdata_source1.arrival_time" +
                    " GROUP BY apcdata_source1.min_stop_id, TEMP.min_stop_id;";
            stmt.executeUpdate(sql);
            System.out.println("11 is done successfully.");

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

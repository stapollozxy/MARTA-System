package edu.gatech.DB;

public class DBUtil {
//    public static final String DB_NAME = "martadb";
//    public static final String PASSWORD = "cs6310";
//    public static final String Path = "/home/student/Desktop/";

    public static final String DB_NAME = "testdb";
    public static final String PASSWORD = "123123";
    public static final String Path = "/Users/kaidizhang/GitHub/CS6310_AS8/other file/"; //apcdata_week2.csv

    private static final String HOSTNAME = "localhost";
    private static final String PORT_NUM = "5432"; // change it to your mysql port number
    public static final String USERNAME = "postgres";
    public static final String URL = "jdbc:postgresql://" + HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME;
    public static final String DELIMITER = "\',\'";


}

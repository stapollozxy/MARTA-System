package edu.gatech.rpc;

import edu.gatech.DB.DBUtil;
import edu.gatech.DB.TableCreation;
import edu.gatech.Marta.SimDriver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "command", urlPatterns = {"/command"})
public class Command extends HttpServlet {
    private SimDriver sd = new SimDriver();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bf = request.getReader();
        String content = bf.readLine();
        String[] tokens = content.split(",");
        //response.setContentType("text/html");
        //response.setContentType("application/json");
        try{

            response.setContentType("text/html");
            response.addHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = response.getWriter();
            String temp;

            if (tokens[0].equals("upload_real_data")) {
            // upload sent to main function
                File f = new File(DBUtil.Path + tokens[1] + ".csv");
                if(!f.exists() || f.isDirectory()) {
                    temp = "invalid input";

                } else {
                    String[] args = new String[1];
                    args[0] = tokens[1] + ".csv";
                    TableCreation.main(args);
                    temp = sd.runInterpreter("upload_real_data");
                }
            } else if (tokens[0].equals("add_event")) {
                temp = sd.runInterpreter("add_event,"+ tokens[1] + ",move_vehicle," + tokens[2]);
            } else if (tokens[0].equals("quit")) {
                temp = sd.runInterpreter(content);
                sd = new SimDriver(); // start new simulation
            } else if (tokens[0].equals("step_once") || tokens[0].equals("step_multi") || tokens[0].equals("system_report") || tokens[0].equals("add_stop")
                    || tokens[0].equals("add_route") || tokens[0].equals("extend_route") || tokens[0].equals("add_vehicle")) {
                temp = sd.runInterpreter(content);
            } else { //command not recognized
                temp = sd.runInterpreter(content);
            }
            out.print(temp);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

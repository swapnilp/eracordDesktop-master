/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eracorddesktop;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.Date;
import javax.management.Query;

/**
 *
 * @author lenovo
 */
public class jdbcConnection {

    String dburl = "jdbc:mysql://localhost:3306/eracord?zeroDateTimeBehavior=convertToNull";
    //String db = "jdbc:mysql://localhost:3306/eracord_development";

    void createStudents(int id, String f_name, String l_name, String m_name, int organisation_id) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(dburl, "root", "satish")) {
                Statement stmt = con.createStatement();
                int count = 0;
                String check_query = "select id from students where id = " + id;
                ResultSet rs = stmt.executeQuery(check_query);
                while (rs.next()) {
                    //count = Integer.parseInt(rs.getString("count(*)"));
                    count++;
                }
                if (count == 0) {
                    String query = "insert into students (id,first_name, last_name, middle_name, organisation_id) values(" + id + ",'" + f_name + "','" + l_name + "','" + m_name + "','" + organisation_id + "' );";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void all_students() {

    }

    void create_last_fetch(int organisation_id) {
        //create new record after server students fetch
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            String query = "insert into fetch_data (organisation_id) values(" + organisation_id + ");";
            stmt.executeUpdate(query);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    String last_fetch_date() {
        //get last fetch record timestamp
        String date = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            String query = "select * from fetch_data ORDER BY datetime DESC LIMIT 1";
            ResultSet rs = stmt.executeQuery(query);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss");
            while (rs.next()) {
                date = simpleDateFormat.format(rs.getTimestamp("datetime")).toString();
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return date;
    }

    void remove_other_org_students(String org_ids) {
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            String query = "delete from students where organisation_id not in (" + org_ids + ") OR organisation_id is null;";
            System.out.println(query);
            stmt.executeUpdate(query);
            System.out.println("asdasdasd");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    int find_id_exits_or_not(String a) {
        ResultSet rs = null;
        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            String query = "select * from students where id =(" + a + ")";
            rs = stmt.executeQuery(query);
            if (rs.next()) { 
                count++;
            }           
            
        } catch (Exception e) {
            System.out.println(e);
        }
        return count;
    }
    void assign_id_card(String a){
          try {
                    System.out.println("insert attendances");
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(dburl, "root", "satish");
                    Statement stmt = con.createStatement();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    String time = dateFormat.format(date);
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    String date1 = dateFormat1.format(cal.getTime());
                    String query = "insert into attendances (student_id, in_time, date) values (" + a + ", '" + time + "', '" + date1 + "')";
                    stmt.executeUpdate(query);
                    
                } catch (Exception e) {
                    System.out.println(e);
                }
    }
    void get_attendances() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            String query = "select * from attendances";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("student_id");
                System.out.println(id);
            }
            con.close();
            Thread.sleep(100000);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    JsonArrayBuilder get_atendance_records(String sdate, int student_id) {
        String query = "";
        JsonArrayBuilder jr = Json.createArrayBuilder();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = con.createStatement();
            if (("").equals(sdate)) {
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss");
                query = "select * from attendances";
            } else {
                query = "select * from attendances";
                //query = "select * from attendances where date >= "+ sdate;
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Date date = rs.getDate("date");
                int id = rs.getInt("student_id");
                if (date != null) {
                    jr.add(Json.createObjectBuilder()
                            .add("date", date.toString())
                            .add("student_id", "" + id));
                }
                System.out.println("STUDENT_ID: " + id + " DATE: " + date);
            }
        } catch (Exception e) {
            System.err.println("ID and STUDENT ID ERROR" + e);
            AdminDesk adObj = new AdminDesk();
            adObj.Logout();
        }
        return jr;
    }

    ResultSet filterStudent() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dburl, "root", "satish");
            Statement stmt = conn.createStatement();
            pstmt = conn.prepareStatement("select id, first_name, middle_name, last_name, id_card, organisation_id from students");
            rs = pstmt.executeQuery();
        } catch (Exception e) {
            System.err.println(e);
        }
        return rs;
    }

    public static void main(String args[]) {
    }
}

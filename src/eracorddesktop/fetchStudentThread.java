package eracorddesktop;

import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonValue;

public class fetchStudentThread extends Thread {

    jdbcConnection jdbcCon = new jdbcConnection();

    public String run(String auth_token) {
        String date = "";
        try {
            int page = 1;
            int arrySize = 0;
            do {
                JsonObject jsonObj = new httpConnection().doGet("/students/sync_organisation_students.json?page=" + page, auth_token);
                if (jsonObj.getBoolean("success")) {

                    JsonArray getArray = jsonObj.getJsonArray("students");
                    arrySize = getArray.size();
                    System.out.println(arrySize);
                    if (arrySize > 0) {
                        store_student(getArray);
                        date = jdbcCon.last_fetch_date();
                    }
                }
                page = page + 1;
                
            } while (arrySize != 0);
        } catch (Exception e) {
            System.out.println(e);
            AdminDesk ad = new AdminDesk();
            ad.Logout();
        }

        return date;
    }

    void store_student(JsonArray studentJson) {
        for (int i = 0; i < studentJson.size(); i++) {
            JsonObject jsonObj = studentJson.getJsonObject(i);
            int id = jsonObj.getInt("id");
            JsonValue first_name = jsonObj.get("first_name");
            JsonValue last_name = jsonObj.get("last_name");
            JsonValue middle_name = jsonObj.get("middle_name");
            int organisation_id = jsonObj.getInt("organisation_id");
            jdbcCon.createStudents(id, first_name.toString(), last_name.toString(), middle_name.toString(), organisation_id);
        }
        // created new fetch record while fetching student 
        jdbcCon.create_last_fetch(1);
    }

    public static void main(String[] args) {
        fetchStudentThread fst = new fetchStudentThread();
        fst.setName("fetchStudentThread");
        fst.start();
    }
}

package com.dental.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;
import com.dental.util.JsonUtil;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:4567/api";

    public User findByUsername(String username) {
        Object parsed = JsonUtil.parse(request("GET", "/users/username/" + username));
        return parsed == null ? null : userFromMap(JsonUtil.asMap(parsed));
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (Object value : JsonUtil.asList(JsonUtil.parse(request("GET", "/users")))) {
            users.add(userFromMap(JsonUtil.asMap(value)));
        }
        return users;
    }

    public void addUser(User user) {
        request("POST", "/users", JsonUtil.toJson(userToMap(user)));
    }

    public void editUser(User user) {
        request("PUT", "/users/" + user.getStaffId(), JsonUtil.toJson(userToMap(user)));
    }

    public void removeUser(String staffId) {
        request("DELETE", "/users/" + staffId, null);
    }

    public List<Doctor> getDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        for (Object value : JsonUtil.asList(JsonUtil.parse(request("GET", "/doctors")))) {
            doctors.add(doctorFromMap(JsonUtil.asMap(value)));
        }
        return doctors;
    }

    public Doctor addDoctor(String name, double consultationFee) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("consultationFee", consultationFee);
        Object parsed = JsonUtil.parse(request("POST", "/doctors", JsonUtil.toJson(map)));
        return doctorFromMap(JsonUtil.asMap(parsed));
    }

    public void editConsultationFee(String doctorId, double consultationFee) {
        request("PUT", "/doctors/" + doctorId + "/fee", String.valueOf(consultationFee));
    }

    public List<TreatmentItem> getTreatments(String doctorId) {
        List<TreatmentItem> treatments = new ArrayList<>();
        for (Object value : JsonUtil.asList(JsonUtil.parse(request("GET", "/doctors/" + doctorId + "/treatments")))) {
            Map<String, Object> map = JsonUtil.asMap(value);
            treatments.add(new TreatmentItem(JsonUtil.asString(map.get("name")), JsonUtil.asDouble(map.get("cost"))));
        }
        return treatments;
    }

    public void updateTreatments(String doctorId, List<TreatmentItem> treatments) {
        List<Object> list = new ArrayList<>();
        for (TreatmentItem treatment : treatments) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", treatment.getName());
            map.put("cost", treatment.getCost());
            list.add(map);
        }
        request("PUT", "/doctors/" + doctorId + "/treatments", JsonUtil.toJson(list));
    }

    public Appointment getAppointment(String id) {
        Object parsed = JsonUtil.parse(request("GET", "/appointments/" + id));
        return parsed == null ? null : appointmentFromMap(JsonUtil.asMap(parsed));
    }

    public List<Appointment> searchAppointments(String query) {
        String encoded = URLEncoder.encode(query == null ? "" : query, StandardCharsets.UTF_8);
        return appointmentsFromJson(request("GET", "/appointments/search?query=" + encoded));
    }

    public List<Appointment> getAppointmentsByDate(String from, String to) {
        return appointmentsFromJson(request("GET", "/appointments/date?from=" + from + "&to=" + to));
    }

    public List<Appointment> getUpcomingAppointments(String from) {
        return appointmentsFromJson(request("GET", "/appointments/upcoming?from=" + from));
    }

    public List<Appointment> getAppointmentsByStaff(String staffId) {
        return appointmentsFromJson(request("GET", "/appointments/staff/" + staffId));
    }

    public int countAppointments(String from, String to, String dentist) {
        String path = "/appointments/count?from=" + from + "&to=" + to;
        if (dentist != null) {
            path += "&dentist=" + URLEncoder.encode(dentist, StandardCharsets.UTF_8);
        }
        return Integer.parseInt(request("GET", path).trim());
    }

    public Appointment addAppointment(Appointment appointment) {
        Object parsed = JsonUtil.parse(request("POST", "/appointments", JsonUtil.toJson(appointmentToMap(appointment))));
        return appointmentFromMap(JsonUtil.asMap(parsed));
    }

    public void updateAppointment(Appointment appointment) {
        request("PUT", "/appointments/" + appointment.getId(), JsonUtil.toJson(appointmentToMap(appointment)));
    }

    // json mapping

    private List<Appointment> appointmentsFromJson(String body) {
        List<Appointment> appointments = new ArrayList<>();
        for (Object value : JsonUtil.asList(JsonUtil.parse(body))) {
            appointments.add(appointmentFromMap(JsonUtil.asMap(value)));
        }
        return appointments;
    }

    private User userFromMap(Map<String, Object> map) {
        return new User(JsonUtil.asString(map.get("staffId")), JsonUtil.asString(map.get("name")),
                JsonUtil.asString(map.get("username")), JsonUtil.asString(map.get("password")),
                JsonUtil.asString(map.get("role")));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("staffId", user.getStaffId());
        map.put("name", user.getName());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", user.getRole());
        return map;
    }

    private Doctor doctorFromMap(Map<String, Object> map) {
        return new Doctor(JsonUtil.asString(map.get("id")), JsonUtil.asString(map.get("name")),
                JsonUtil.asDouble(map.get("consultationFee")));
    }

    private Appointment appointmentFromMap(Map<String, Object> map) {
        List<TreatmentItem> items = new ArrayList<>();
        Object itemsValue = map.get("items");
        if (itemsValue != null) {
            for (Object value : JsonUtil.asList(itemsValue)) {
                Map<String, Object> itemMap = JsonUtil.asMap(value);
                items.add(new TreatmentItem(JsonUtil.asString(itemMap.get("name")), JsonUtil.asDouble(itemMap.get("cost"))));
            }
        }
        return new Appointment(JsonUtil.asString(map.get("id")), JsonUtil.asString(map.get("patientName")),
                JsonUtil.asString(map.get("patientAddress")), JsonUtil.asString(map.get("patientPhone")),
                JsonUtil.asString(map.get("doctor")), JsonUtil.asDouble(map.get("consultationFee")), items,
                JsonUtil.asString(map.get("date")), JsonUtil.asString(map.get("time")),
                JsonUtil.asString(map.get("handledBy")), JsonUtil.asString(map.get("handledById")));
    }

    private Map<String, Object> appointmentToMap(Appointment appointment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", appointment.getId());
        map.put("patientName", appointment.getPatientName());
        map.put("patientAddress", appointment.getPatientAddress());
        map.put("patientPhone", appointment.getPatientPhone());
        map.put("doctor", appointment.getDoctor());
        map.put("consultationFee", appointment.getConsultationFee());
        map.put("date", appointment.getDate());
        map.put("time", appointment.getTime());
        map.put("handledBy", appointment.getHandledBy());
        map.put("handledById", appointment.getHandledById());
        List<Object> items = new ArrayList<>();
        for (TreatmentItem item : appointment.getItems()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("name", item.getName());
            itemMap.put("cost", item.getCost());
            items.add(itemMap);
        }
        map.put("items", items);
        return map;
    }

    // http helpers

    private String request(String method, String path) {
        return request(method, path, null);
    }

    private String request(String method, String path, String json) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + path).openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (json != null) {
                conn.setDoOutput(true);
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }
            int code = conn.getResponseCode();
            if (code >= 400) {
                throw new RuntimeException("Server error: " + code + ": " + readBody(conn.getErrorStream()));
            }
            return readBody(conn.getInputStream());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Could not reach the server. Start the server first.", e);
        }
    }

    private String readBody(InputStream in) throws Exception {
        if (in == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
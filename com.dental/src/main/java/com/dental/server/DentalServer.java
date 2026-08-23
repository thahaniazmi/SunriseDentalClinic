package com.dental.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.AppointmentServiceImpl;
import com.dental.service.DoctorService;
import com.dental.service.DoctorTreatmentService;
import com.dental.service.UserService;
import com.dental.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class DentalServer {
    public static void main(String[] args) {
        start();
    }

    public static void start() {
        UserService userService = new UserService();
        DoctorService doctorService = new DoctorService();
        DoctorTreatmentService treatmentService = new DoctorTreatmentService();
        AppointmentService appointmentService = new AppointmentServiceImpl();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);
            server.createContext("/api", exchange ->
                    handle(exchange, userService, doctorService, treatmentService, appointmentService));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException("Could not start the server", e);
        }
        System.out.println("DentalServer running on http://localhost:4567");
    }

    private static void handle(HttpExchange exchange, UserService userService, DoctorService doctorService,
                               DoctorTreatmentService treatmentService, AppointmentService appointmentService) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getRawQuery();
            String body = readBody(exchange.getRequestBody());

            String response;
            if (path.equals("/api/users") && method.equals("GET")) {
                response = toJsonUsers(userService.getAllUsers());
            } else if (path.equals("/api/users") && method.equals("POST")) {
                userService.addUser(userFromJson(body));
                response = "ok";
            } else if (path.startsWith("/api/users/username/") && method.equals("GET")) {
                User user = userService.findByUsername(path.substring("/api/users/username/".length()));
                response = user == null ? "null" : toJsonUser(user);
            } else if (path.startsWith("/api/users/") && method.equals("PUT")) {
                userService.editUser(userFromJson(body));
                response = "ok";
            } else if (path.startsWith("/api/users/") && method.equals("DELETE")) {
                userService.removeUser(path.substring("/api/users/".length()));
                response = "ok";
            } else if (path.equals("/api/doctors") && method.equals("GET")) {
                response = toJsonDoctors(doctorService.getAllDoctors());
            } else if (path.equals("/api/doctors") && method.equals("POST")) {
                Doctor doctor = doctorFromJson(body);
                String id = doctorService.addDoctor(doctor.getName(), doctor.getConsultationFee());
                response = toJsonDoctor(doctorService.findById(id));
            } else if (path.startsWith("/api/doctors/") && path.endsWith("/fee") && method.equals("PUT")) {
                double fee = Double.parseDouble(body);
                doctorService.editConsultationFee(path.substring("/api/doctors/".length(), path.length() - "/fee".length()), fee);
                response = "ok";
            } else if (path.startsWith("/api/doctors/") && path.endsWith("/treatments") && method.equals("GET")) {
                String doctorId = path.substring("/api/doctors/".length(), path.length() - "/treatments".length());
                response = toJsonTreatments(treatmentService.getAllForDoctor(doctorId));
            } else if (path.startsWith("/api/doctors/") && path.endsWith("/treatments") && method.equals("PUT")) {
                String doctorId = path.substring("/api/doctors/".length(), path.length() - "/treatments".length());
                treatmentService.updateTreatments(doctorId, treatmentsFromJson(body));
                response = "ok";
            } else if (path.equals("/api/appointments/search") && method.equals("GET")) {
                response = toJsonAppointments(appointmentService.search(queryParam(query, "query")));
            } else if (path.equals("/api/appointments/date") && method.equals("GET")) {
                response = toJsonAppointments(appointmentService.searchByDate(queryParam(query, "from"), queryParam(query, "to")));
            } else if (path.equals("/api/appointments/upcoming") && method.equals("GET")) {
                response = toJsonAppointments(appointmentService.searchUpcoming(queryParam(query, "from")));
            } else if (path.startsWith("/api/appointments/staff/") && method.equals("GET")) {
                response = toJsonAppointments(appointmentService.searchByStaffId(path.substring("/api/appointments/staff/".length())));
            } else if (path.equals("/api/appointments") && method.equals("POST")) {
                Appointment appointment = appointmentService.addAppointment(appointmentFromJson(body));
                response = toJsonAppointment(appointment);
            } else if (path.startsWith("/api/appointments/") && method.equals("GET")) {
                Appointment appointment = appointmentService.findById(path.substring("/api/appointments/".length()));
                response = appointment == null ? "null" : toJsonAppointment(appointment);
            } else if (path.startsWith("/api/appointments/") && method.equals("PUT")) {
                appointmentService.updateAppointment(appointmentFromJson(body));
                response = "ok";
            } else {
                respond(exchange, 404, "not found");
                return;
            }
            respond(exchange, 200, response);
        } catch (Exception e) {
            respond(exchange, 500, "error: " + e.getMessage());
        }
    }

    // ---------- json helpers ----------

    private static Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("staffId", user.getStaffId());
        map.put("name", user.getName());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", user.getRole());
        return map;
    }

    private static Map<String, Object> doctorToMap(Doctor doctor) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", doctor.getId());
        map.put("name", doctor.getName());
        map.put("consultationFee", doctor.getConsultationFee());
        return map;
    }

    private static Map<String, Object> treatmentToMap(TreatmentItem treatment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", treatment.getName());
        map.put("cost", treatment.getCost());
        return map;
    }

    private static Map<String, Object> appointmentToMap(Appointment appointment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", appointment.getId());
        map.put("patientName", appointment.getPatientName());
        map.put("patientPhone", appointment.getPatientPhone());
        map.put("doctor", appointment.getDoctor());
        map.put("consultationFee", appointment.getConsultationFee());
        map.put("date", appointment.getDate());
        map.put("time", appointment.getTime());
        map.put("handledBy", appointment.getHandledBy());
        map.put("handledById", appointment.getHandledById());
        List<Object> items = new ArrayList<>();
        for (TreatmentItem item : appointment.getItems()) {
            items.add(treatmentToMap(item));
        }
        map.put("items", items);
        return map;
    }

    private static String toJsonUser(User user) {
        return JsonUtil.toJson(userToMap(user));
    }

    private static String toJsonUsers(List<User> users) {
        List<Object> list = new ArrayList<>();
        for (User user : users) {
            list.add(userToMap(user));
        }
        return JsonUtil.toJson(list);
    }

    private static String toJsonDoctor(Doctor doctor) {
        return JsonUtil.toJson(doctorToMap(doctor));
    }

    private static String toJsonDoctors(List<Doctor> doctors) {
        List<Object> list = new ArrayList<>();
        for (Doctor doctor : doctors) {
            list.add(doctorToMap(doctor));
        }
        return JsonUtil.toJson(list);
    }

    private static String toJsonTreatments(List<TreatmentItem> treatments) {
        List<Object> list = new ArrayList<>();
        for (TreatmentItem treatment : treatments) {
            list.add(treatmentToMap(treatment));
        }
        return JsonUtil.toJson(list);
    }

    private static String toJsonAppointment(Appointment appointment) {
        return JsonUtil.toJson(appointmentToMap(appointment));
    }

    private static String toJsonAppointments(List<Appointment> appointments) {
        List<Object> list = new ArrayList<>();
        for (Appointment appointment : appointments) {
            list.add(appointmentToMap(appointment));
        }
        return JsonUtil.toJson(list);
    }

    private static User userFromJson(String body) {
        Map<String, Object> map = JsonUtil.asMap(JsonUtil.parse(body));
        return new User(JsonUtil.asString(map.get("staffId")), JsonUtil.asString(map.get("name")),
                JsonUtil.asString(map.get("username")), JsonUtil.asString(map.get("password")),
                JsonUtil.asString(map.get("role")));
    }

    private static Doctor doctorFromJson(String body) {
        Map<String, Object> map = JsonUtil.asMap(JsonUtil.parse(body));
        return new Doctor(JsonUtil.asString(map.get("id")), JsonUtil.asString(map.get("name")),
                JsonUtil.asDouble(map.get("consultationFee")));
    }

    private static List<TreatmentItem> treatmentsFromJson(String body) {
        List<TreatmentItem> treatments = new ArrayList<>();
        for (Object value : JsonUtil.asList(JsonUtil.parse(body))) {
            Map<String, Object> map = JsonUtil.asMap(value);
            treatments.add(new TreatmentItem(JsonUtil.asString(map.get("name")), JsonUtil.asDouble(map.get("cost"))));
        }
        return treatments;
    }

    private static Appointment appointmentFromJson(String body) {
        Map<String, Object> map = JsonUtil.asMap(JsonUtil.parse(body));
        List<TreatmentItem> items = new ArrayList<>();
        Object itemsValue = map.get("items");
        if (itemsValue != null) {
            for (Object value : JsonUtil.asList(itemsValue)) {
                Map<String, Object> itemMap = JsonUtil.asMap(value);
                items.add(new TreatmentItem(JsonUtil.asString(itemMap.get("name")), JsonUtil.asDouble(itemMap.get("cost"))));
            }
        }
        return new Appointment(JsonUtil.asString(map.get("id")), JsonUtil.asString(map.get("patientName")),
                JsonUtil.asString(map.get("patientPhone")), JsonUtil.asString(map.get("doctor")),
                JsonUtil.asDouble(map.get("consultationFee")), items, JsonUtil.asString(map.get("date")),
                JsonUtil.asString(map.get("time")), JsonUtil.asString(map.get("handledBy")),
                JsonUtil.asString(map.get("handledById")));
    }

    // ---------- http helpers ----------

    private static String readBody(InputStream in) throws IOException {
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void respond(HttpExchange exchange, int code, String text) {
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        } catch (IOException e) {
            // the client may have closed the connection early
        }
    }

    private static String queryParam(String query, String key) {
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(key)) {
                return URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
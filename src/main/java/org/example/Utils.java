package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {


    public int getListeningPort(String serverUrl){
        HttpURLConnection connection = null;
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(serverUrl + "/getListeningPort");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return Integer.parseInt(content.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createAccount(String name, String username, String password){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("nome", name);
            jsonBody.put("administrador", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
                HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/users/signup";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonBody)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assert response.statusCode() ==  200 : "Connection Error";
            //Assert.assertEquals(response.body(), "{\"message\":\"ok\"}");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginAccount(String username, String password){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("administrador", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/users/login";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonBody)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assert response.statusCode() ==  200 : "Connection Error";
            //Assert.assertEquals(response.body(), "{\"message\":\"ok\"}");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAvailableShows() {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/espetaculos";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Available Shows:");
                System.out.println(response.body()); // Print or process the list of shows as needed
            } else {
                System.out.println("Error retrieving shows. HTTP Status Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccount(String oldUsername, String newName, String newUsername, String newPassword) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome", newName);
            jsonBody.put("username", newUsername);
            jsonBody.put("password", newPassword);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/users/update?oldUsername=" + oldUsername;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonBody)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assert response.statusCode() == 200 : "Connection Error";
            // Assert.assertEquals(response.body(), "{\"message\":\"ok\"}");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchShows(String pais, String local, String tipo, String data_hora) {
        String url = "http://localhost:8080/espetaculos";
        try {
            URI uri = new URI(url + "?pais=" + pais + "&local=" + local + "&tipo=" + tipo + "&data_hora=" + data_hora);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkShowExists(int showId) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/espetaculos/check?idEspetaculo=" + showId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && Boolean.parseBoolean(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSeatsForShow(int showId) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/lugares?showId=" + showId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

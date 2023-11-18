package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {


    public int getListeningPort(String serverUrl){
        HttpURLConnection connection = null;
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(serverUrl + "/listeningPort/get");
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

    public boolean loginAccount(String username, String password){
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

            if (response.statusCode() == 200) {
                System.out.println("Login Successful");
            } else if (response.statusCode() == 401) {
                System.out.println("Invalid username or password");
            } else {
                System.out.println("Error: " + response.statusCode());
            }
            return response.statusCode() == 200;
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

//    OLD SEARCH SHOWS - FILTERING ON THE SERVER SIDE
//    public void searchShows(String pais, String local, String tipo, String data_hora) {
//        String url = "http://localhost:8080/espetaculos";
//        try {
//            URI uri = new URI(url + "?pais=" + pais + "&local=" + local + "&tipo=" + tipo + "&data_hora=" + data_hora);
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println(response.body());
//        } catch (URISyntaxException | InterruptedException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//  NEW SEARCH SHOES - FILTERING ON THE CLIENT SIDE
    public void searchShows(String pais, String local, String tipo, String data_hora) {
        String url = "http://localhost:8080/espetaculos";
        try {
            URI uri = new URI(url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON array directly
            JSONArray espArray = new JSONArray(response.body());

            // Filter the array based on the provided parameters
            List<JSONObject> filteredEspetaculos = new ArrayList<>();
            for (int i = 0; i < espArray.length(); i++) {
                JSONObject espObject = espArray.getJSONObject(i);
                if ((pais == null || espObject.getString("pais").equals(pais))
                        && (local == null || espObject.getString("local").equals(local))
                        && (tipo == null || espObject.getString("tipo").equals(tipo))
                        && (data_hora == null || espObject.getString("data_hora").equals(data_hora))) {
                    filteredEspetaculos.add(espObject);
                }
            }

            // Print or process the filteredEspetaculos as needed
            System.out.println(filteredEspetaculos);
        } catch (URISyntaxException | InterruptedException | IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkShowExists(int showId) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/espetaculos/check?idEspetaculo=" + showId;

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create(serviceUrl))
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
        String serviceUrl = "http://localhost:8080/lugares?idEspetaculo=" + showId;

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

    public void getPaidReservations(int id_utilizador) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/reservas/pagas?id_utilizador=" + id_utilizador;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Paid Reservations:");
                System.out.println(response.body()); // Print or process the list of shows as needed
            } else {
                System.out.println("Error retrieving shows. HTTP Status Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void getUnpaidReservations(int id_utilizador) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/reservas/nao-pagas?id_utilizador=" + id_utilizador;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Unpaid Reservations:");
                System.out.println(response.body()); // Print or process the list of shows as needed
//                JSONArray reservationsArray = new JSONArray(response.body()); //TODO
//
//                for (int i = 0; i < reservationsArray.length(); i++) {
//                    JSONObject reservationObject = reservationsArray.getJSONObject(i);
//                    System.out.println("ID: " + reservationObject.getInt("id") +
//                            ", Data_Hora: " + reservationObject.getString("data_hora"));
//                }
            } else {
                System.out.println("Error retrieving shows. HTTP Status Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void payReservation(int id) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/reservas/" + id + "/pagamento";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Reservation marked as paid.");
            } else {
                System.out.println("Error marking reservation as paid. HTTP Status Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReservation(int id) {
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/reservas/apaga-reserva/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Reservation deleted successfully.");
            } else {
                System.out.println("Error deleting reservation. HTTP Status Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeReservation(int idEspetaculo, int idLugar, int idUtilizador) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("idEstpetaculo", idEspetaculo);
            jsonBody.put("idLugar", idLugar);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        HttpClient client = HttpClient.newHttpClient();
        String serviceUrl = "http://localhost:8080/reservas/criar-reserva?idUtilizador=" + idUtilizador;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("content-type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonBody)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Reservation Successful");
            } else if (response.statusCode() == 401) {
                System.out.println("Reservation Failed");
            } else {
                System.out.println("Error: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}

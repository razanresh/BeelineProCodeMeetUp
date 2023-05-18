package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final String GET_URL = "https://procodeday-01.herokuapp.com/meet-up/get-country-list";
    private static final String POST_URL = "https://procodeday-01.herokuapp.com/meet-up/post-request";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            // Шаг 1: Выполняем GET-запрос
            String responseJson = sendGetRequest(GET_URL);

            // Шаг 2: Преобразуем JSON-ответ в список объектов Country
            List<Country> countries = objectMapper.readValue(responseJson, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Country.class));

            // Шаг 3: Группируем города по странам и сортируем города в каждой стране
            Map<String, List<String>> groupedCities = countries.stream()
                    .collect(Collectors.groupingBy(Country::getCountry,
                            Collectors.mapping(Country::getCity, Collectors.toList())));
            groupedCities.values().forEach(cityList -> cityList.sort(Comparator.naturalOrder()));

            // Шаг 4: Считаем количество городов в каждой стране
            List<Result> results = groupedCities.entrySet().stream()
                    .map(entry -> new Result(entry.getKey(), entry.getValue(), entry.getValue().size()))
                    .collect(Collectors.toList());

            // Шаг 7: Создаем объект Student и заполняем данные
            Student student = new Student();
            student.setName("Шерназар Сталбеков");
            student.setPhone("0755777533");
            student.setGithubUrl("https://github.com/razanresh");

            // Шаг 9: Создаем объект, содержащий student и список result, и преобразуем его в JSON
            PostData postData = new PostData();
            postData.setStudent(student);
            postData.setResult(results);
            String postJson = objectMapper.writeValueAsString(postData);

            // Шаг 10: Выполняем POST-запрос с результатами
            sendPostRequest(POST_URL, postJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sendGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static void sendPostRequest(String url, String json) throws IOException {
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("POST запрос выполнен успешно");
            } else {
                System.out.println("Ошибка выполнения POST запроса: " + response.code());
            }
        }
    }
}



package push700.demo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class mainController {

    private static final String PRIVATE_KEY_PATH = "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC3NqRL59XG8Zp/\nqFVZZf2jps/OnQKIDm+xpyI5lDm9joYHtuMW9q2Z/Mqxqoc7TFqgDaMJbwdyv71T\nwzdcgR6aSiuz4cDzfS5Wfova9lV8EKlYFU4w5K07Gun9NIPy/uPXBFfPrhrzi07/\nZi1dHuOeukExZqwYPf56iXzoscjkLeYg4Q9O3ESS1FCZtc+lmjRzTUHhHf8ypFZm\nMxmXusuSgOD3qF1KAR4A3k8/PlxIwlhbB1I/mtbjtmtqZyOxZHnmXVRoFJ/BmH1C\nfCF0B5VdqzMwSL7y36wfQxIv3tIhidTBmY31gVk7pjGVWYxjxTKWaH7CqKPLBYcI\nVmqEKhcnAgMBAAECggEAQSKsg43vGzT/A/6RJWDUrU9bVu3MHhnfzM2KJeVUJ9eZ\nP/ndv2bArlvIpiRlwoexOnDOs0ZlY/7V7Qcc0gbXnevboK6UfhxVkouX5icomhyE\nUBv2+fv6uGYnpQz8EjJbRzC0lVGlJEu1MKifXRljvCUkk08YxN+ZbvAdV88JCFHM\n7cH12gI4NQvGT7ObRMw78ppnLZUcAZZoMLsohl5F7bHsWwDBoxX+rQZ2wB7v1YD1\nW4p5pcjhfSo22lNwG1n/JKppQEBRYK2mPoX2U/SQntF1bs2ZJLXD50bf87mkLLis\nNGOTLNMfArke3kSRWs9fOSenNWgRQ7QJriej/eRhPQKBgQDjpnGlx06g29MIKUy6\n9sus3OKIt4BJTE049ems+yGP40JCFLyzYkBxtGPu3c6WPwlq3I5/eHi8U/3XXJ5q\nOxaQ4+O5a05Tu1rkBcuuUWpRgJra7WQb7aozEQwTzEkvNn6jAj9vvhg9iR735r/z\nBCaqilbmwCrWxZnuMqDRrQgvFQKBgQDOB4qmEJCb9dpYZ2x4gQnzu4Gg/CtPJjjF\nNWbXEWV8IwJSS1lEPbBWY2sI/HjBmYhgR2vwSBSf77zA2reVvTGS58qnk77j29cQ\nvTvN158LUYBkH/ElgLrTQXv5r3Cyr78imGB1AE+rNHrTOHsoQ/Yqj5a6qGAje7T3\nZTkFTqYcSwKBgBng3+Zyg7OxiYnR+BEA4SJjcbUM9x5d+CFTgx7Q5X3P3cZZSSIz\nNxQ1525N9KALxmxJakF/VmpCOgbzVmbJFMWfwlFfq2XFVI27Qu4r/yQItJHSjqDG\nIaTweSj+/grJ7RdmuANh3quQHN8dchCUFjkoxLwyeCw2BkuXocR0IQwJAoGAKdiQ\nSaBtrq+ltHT1LBiRa+a2snrlVQMfXra095O5+BeoPgy9ycV5zmIM+FtHL5M+hsea\n3cfWIJHx1X3W8dvmeL8JgzXX/LHHIcg9oFZeIE9ic3OlaAeYapYPVyxSCIj3mlA2\nryzsAYr8M3ByW0BKfGSDqc/PDwXuQJ1XkoNh67cCgYAC6j/YxDKKsxhthcPJ+AKV\nKXhQQG51kcPxnTqDJg9sP5nJR8Nm/Rlaxy4hYqg71zwNn53UuLdsbLVcyYBFjSb/\n1MU/1jT7joIa/iM7P9c59Or8+rqWYmLbgLqVjjtTfD3Ar6iACl9+rYp+7kBFdQIb\n9r8TFBl6cOdjb82wKgBCoQ==\n-----END PRIVATE KEY-----\n";
    private static final String CLIENT_EMAIL = "firebase-adminsdk-wbvfu@notifiapp-dce12.iam.gserviceaccount.com";
    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String PROJECT_ID = "notifiapp-dce12";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
    @GetMapping()
    public void getAll(@RequestParam("token") String token) {
        try {
            String jwt = createJWT(CLIENT_EMAIL, PRIVATE_KEY_PATH, TOKEN_URI);
            String accessToken = getAccessToken(jwt);

            if (accessToken != null) {
                sendPushNotification(accessToken, token, "Пора сыграть партию в Шахматы", "перейти в chess.com");
            } else {
                System.out.println("Failed to get access token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String createJWT(String clientEmail, String privateKeyPath, String tokenUri) throws Exception {
        // Prepare JWT header
        JSONObject header = new JSONObject();
        header.put("alg", "RS256");
        header.put("typ", "JWT");

        // Prepare JWT claims
        long now = System.currentTimeMillis() / 1000;
        JSONObject claims = new JSONObject();
        claims.put("iss", clientEmail);
        claims.put("scope", "https://www.googleapis.com/auth/firebase.messaging");
        claims.put("aud", tokenUri);
        claims.put("exp", now + 3600);
        claims.put("iat", now);

        // Encode JWT parts
        String headerBase64 = Base64.getUrlEncoder().encodeToString(header.toString().getBytes());
        String claimsBase64 = Base64.getUrlEncoder().encodeToString(claims.toString().getBytes());

        String data = headerBase64 + "." + claimsBase64;

        // Sign JWT with private key
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);
        byte[] signature = signWithRS256(data, privateKey);
        String signatureBase64 = Base64.getUrlEncoder().encodeToString(signature);

        return data + "." + signatureBase64;
    }

    public static PrivateKey loadPrivateKey(String privateKeyString) throws Exception {
        String keyString = privateKeyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Удалите все пробельные символы (включая символы новой строки)
        byte[] decoded = Base64.getDecoder().decode(keyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(decoded));
    }

    public static byte[] signWithRS256(String data, PrivateKey privateKey) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return signature.sign();
    }

    public static String getAccessToken(String jwt) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(TOKEN_URI);

        // Фо
        // рмируем параметры для запроса
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
        params.add(new BasicNameValuePair("assertion", jwt));

        // Устанавливаем параметры в теле запроса
        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        HttpResponse response = client.execute(post);
        String responseBody = EntityUtils.toString(response.getEntity());

        JSONObject responseJson = new JSONObject(responseBody);
        System.out.println(responseJson);
        return responseJson.optString("access_token", null);
    }


    public static void sendPushNotification(String accessToken, String deviceToken, String title, String body) throws IOException {
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);

        message.put("message", new JSONObject()
                .put("token", deviceToken)
                .put("notification", notification));

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(FCM_URL);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(message.toString()).build());

        HttpResponse response = client.execute(post);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("FCM Response: " + responseBody);
    }

}

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

    private static final String PRIVATE_KEY_PATH = "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCi7oEfUKWsh+1Q\nyT7+aaLluIT0gYkqDtg4MRgHLlbOF+OdahIkXHYJdnvrxakP1LWb7C1dWz8VXZbK\nfE6/U1FdrCXhoS0rETKbLnKeskjIiop/iIK4o9z2XGrMpSXRdhOKkCAGxCsXkWCH\nNU9Rd0oilVJmQJ/mIvzExS+0UFuKmwmfeFxN5RGGKasWnAd53AhwlgknO1J3HMfl\n8yN4n94CA0nJSD4fP5+x6BzqzYLbq4Bi3Yblnt7ON5fl96VgO2TDUgQ9j+grjijr\nRExMdDxGh1T/2k3Z7wRBRC4KJdW29IkgrCTpI0igErk3g0PXG+2bNqt8ZPguwIZQ\n2SGSSjqhAgMBAAECggEAHYAuSHd/e+kDpuo9We7SFCKQDj5rEb6s1yXKejhhdsD8\nEUs8nDFSnP2DTAoWxZtHw8LkZuAn4Kw4qpKN5pYFKD8Mf9lBVqgvP8pN7h6NT6RI\nFfXnZ1uiHUhX/RRU7+Nff+n/JoJ74/FKZ0jXgwJknbToPcPrP83rcwxaCh23qXWF\naTa+6EDtTF9sk4tR86OUnh0qGLgXuS2dJMAgyDYZ4zkbsEmw2qgDVC8AxHPhDld2\nzSsxyJuYpNgp49lgLxeoWrrbjT7ntqKqDDHdnuphsUtI++b0BUrG8oeYVk6OR+to\n7cwyQZXlk07PHZsl/cEHKBwb5sz+GaIxMvlpEIMnLQKBgQDUdUgVVAL2NZKC8vqS\nGnA9Yx5TY26IGtFTjYwRA9UuXuOfsl0UytydOytEuks0DdsNm3MlUTwFiIdaS0dX\ngvk2rENPpAlYm4FPL6/T17T7NYwjKyu3lU2opo+O1JYMSU79zqgTc7lkbCiZhN6/\nQLmOE5lPebuy0eF01kRFmaQenQKBgQDEUsrfV00jd/f3As2SU12zMBOhSzhPYlKe\n7fUCwQO0FzDWzE2Hi7GsYrMCBaOD3YZh58gezV7YxDpv8kiIk/stK5MvHXNGUv5d\ne0U2X/XvnbflBMbWbFodsnUXCDzJALSnj7bX+X7xp6/5KzqjFYdsNO7Leo3waGmU\nvbCWDWMq1QKBgHbGxrDd0fYRx0IcU0ehLkSziD24ZhADZimfBcJxcRpGhF1Am1dH\nfoUMkkkPGYIViPSXho1DWBoD1R/+2/ipyWfucIcyvuLhtS4vC86UVqLmsdzrgrpU\n5yC1VabVZLmjtlygz0hotynUafJB3UHxDovGmzxaYzw6qB/otixvE9jlAoGAUTYG\nYdkVOcTYuD9allsR+Zu7ow8gngYIM2Iv5hG93HNzaodc0uNAfhTeA8odV+D9Q+bD\na6p8v6J8oe5Rft3oG0lZOzYthzuxqS3i9ZlAfPx7I6uULQLpTDuUIBDyRBucn2vd\nsP2uO3yoWKQ/P1Maj/JdjaKGsJoggrztHUUwbnkCgYBtXDudX491bpYTh+OoAq+u\nwOpGMRLWP+WMX+I4bffP/fFfe7iHy/ZnYu3r0bfFXlfeX2xEs+wv8+0KyxP+f9fE\nlifaYwnaXKrTuSasibUHfTe3iRPfa60aKc7UNTPk1ivalbh3sVYJJbYO2clVXCDS\npLLnG0Zi8gE3TgbKgai01A==\n-----END PRIVATE KEY-----\n";
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

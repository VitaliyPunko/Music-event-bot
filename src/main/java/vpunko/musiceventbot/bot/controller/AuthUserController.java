package vpunko.musiceventbot.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/auth/telegram")
@RequiredArgsConstructor
public class AuthUserController {

    private final Map<Long, Boolean> authenticatedUsers = new ConcurrentHashMap<>();
    private final String tgBotToken = "7941376949:AAHeus8Bg2E0aydGgWJEvtdOE1S3col-H1M";


    /**
     * возвращает html со скриптом для аутентификации
     */
    @GetMapping
    public ResponseEntity<Resource> getAuthScript() {
        Resource resource = new ClassPathResource("static/telegram.html");
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=telegram.html");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    /**
     * сюда отправляются данные, полученные после аутентификации
     */
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
    public String authenticate(@RequestBody Map<String, Object> telegramData) {
        System.out.println("XXXXXXXXXX");
        System.out.println(telegramData);
        if (telegramDataIsValid(telegramData)) {
            Long userId = Long.parseLong(telegramData.get("id").toString());

            // ✅ Сохраняем пользователя как авторизованного
            authenticatedUsers.put(userId, true);

            // ✅ Отправляем сообщение в Telegram
            sendTelegramMessage(userId, "✅ Авторизация прошла успешно! Теперь введите /next для продолжения.");

            return "valid"; // ✅ Фронт тоже узнает, что всё прошло успешно
        }
        return "error";
    }

    private void sendTelegramMessage(Long userId, String text) {
        String url = "https://api.telegram.org/bot" + tgBotToken + "/sendMessage";

        Map<String, Object> body = Map.of(
                "chat_id", userId,
                "text", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(url, requestEntity, String.class);
    }

    // Метод проверки авторизации (используется ботом)
    public boolean isUserAuthenticated(Long userId) {
        return authenticatedUsers.getOrDefault(userId, false);
    }

    /**
     * проверяет данные, полученные из телеграм
     */
    private boolean telegramDataIsValid(Map<String, Object> telegramData) {
        //получаем хэш, который позже будем сравнивать с остальными данными
        String hash = (String) telegramData.get("hash");
        telegramData.remove("hash");

        //создаем строку проверки - сортируем все параметры и объединяем их в строку вида:
        //auth_date=<auth_date>\nfirst_name=<first_name>\nid=<id>\nusername=<username>
        StringBuilder sb = new StringBuilder();
        telegramData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n"));
        sb.deleteCharAt(sb.length() - 1);
        String dataCheckString = sb.toString();

        try {
            //генерируем SHA-256 хэш из токена бота
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] key = digest.digest(tgBotToken.getBytes(UTF_8));

            //создаем HMAC со сгенерированным хэшем
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
            hmac.init(secretKeySpec);

            // добавляем в HMAC строку проверки и переводим в шестнадцатеричный формат
            byte[] hmacBytes = hmac.doFinal(dataCheckString.getBytes(UTF_8));
            StringBuilder validateHash = new StringBuilder();
            for (byte b : hmacBytes) {
                validateHash.append(String.format("%02x", b));
            }

            // сравниваем полученный от телеграма и сгенерированный хэш
            return hash.contentEquals(validateHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}

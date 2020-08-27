package dev.elektronisch.screenshot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageUploadUtil {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final MediaType PNG_MEDIA_TYPE = MediaType.parse("image/png");

    private ImageUploadUtil() {
    }

    public static String uploadPNG(final BufferedImage image) {
        final byte[] bytes = toBytes(image);
        if (bytes == null) return null;

        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "demo.png", RequestBody.create(bytes, PNG_MEDIA_TYPE))
                .build();
        final Request request = new Request.Builder().url("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID 5dcea2f68130abc").post(requestBody).build();

        try (final Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            final ResponseBody body = response.body();
            if (body == null) return null;

            final JsonObject parsedResponse = JsonParser.parseString(body.string()).getAsJsonObject();
            return parsedResponse.get("data").getAsJsonObject().get("link").getAsString();
        } catch (final IOException ignored) {
        }
        return null;
    }

    private static byte[] toBytes(final BufferedImage image) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (final IOException ignored) {
        }
        return null;
    }
}

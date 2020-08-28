package dev.elektronisch.screenshot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class UpdateUtil {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    private UpdateUtil() {
    }

    public static String checkForUpdate(final double currentVersion) {
        final Request request = new Request.Builder().url("https://api.github.com/repos/Cytooxien/screenshotTool/releases/latest").build();
        try (final Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            final ResponseBody body = response.body();
            if (body == null) return null;

            final JsonObject parsedResponse = JsonParser.parseString(body.string()).getAsJsonObject();
            final double version = Double.parseDouble(parsedResponse.get("tag_name").getAsString().substring(1));
            if (version > currentVersion) {
                final JsonArray assets = parsedResponse.get("assets").getAsJsonArray();
                if (assets.size() == 1) {
                    final JsonObject downloadableFile = assets.get(0).getAsJsonObject();
                    return downloadableFile.get("browser_download_url").getAsString();
                }
            }
        } catch (final IOException ignored) {
        }
        return null;
    }

    public static void downloadUpdate(final String downloadUrl, final File file) {
        final Request request = new Request.Builder().url(downloadUrl).build();
        try (final Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            final ResponseBody body = response.body();
            if (body == null) return;

            Files.copy(body.byteStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ignored) {
        }
    }
}

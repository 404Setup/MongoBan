package one.tranic.mongoban.common.updater.modrinth;

import com.google.gson.Gson;
import one.tranic.mongoban.common.updater.UpdateRecord;
import one.tranic.mongoban.common.updater.Updater;
import one.tranic.mongoban.common.updater.VersionComparator;
import one.tranic.mongoban.common.updater.modrinth.source.Loaders;
import one.tranic.mongoban.common.updater.modrinth.source.ModrinthVersionSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Consumer;

public class ModrinthUpdate {
    private final String slug;
    private final String localVersion;
    private final String loader;
    private final String gameVersion;
    private final boolean simpleMode;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    /**
     * A default empty {@link UpdateRecord} used when no updates are available.
     */
    UpdateRecord empty = new UpdateRecord(false, "", "", "");

    public ModrinthUpdate(@NotNull String slug, @NotNull String localVersion, @NotNull Loaders loader, @NotNull String gameVersion) {
        this(slug, localVersion, loader.toString(), gameVersion);
    }

    public ModrinthUpdate(@NotNull String slug, @NotNull String localVersion, @NotNull String loader, @NotNull String gameVersion) {
        this(slug, localVersion, loader, gameVersion, true);
    }

    public ModrinthUpdate(@NotNull String slug, @NotNull String localVersion, @NotNull String loader, @NotNull String gameVersion, boolean simpleMode) {
        this.slug = slug;
        this.localVersion = localVersion;
        this.loader = loader;
        this.gameVersion = gameVersion;
        this.simpleMode = simpleMode;
    }

    /**
     * Asynchronously retrieves the latest {@link UpdateRecord} and passes it to the provided {@link Consumer}.
     * <p>
     * This method runs in a separate thread. If an {@link IOException} occurs during the update retrieval,
     * it is caught and the exception stack trace is printed, and {@code null} may be passed to the consumer
     * in case of failure.
     * </p>
     *
     * @param consumer a {@link Consumer} that will process the retrieved {@link UpdateRecord} or {@code null}
     *                 if an error occurs.
     */
    public void getUpdateAsync(@NotNull Consumer<@Nullable UpdateRecord> consumer) {
        Thread.ofVirtual().name("MongoBan Async Updater").start(() -> {
            try {
                consumer.accept(getUpdate());
            } catch (IOException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public UpdateRecord getUpdate() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.modrinth.com/v2/project/" + slug + "/version"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new IOException("Invalid resource");
            } else if (response.statusCode() != 200) {
                throw new IOException("Unexpected code " + response.statusCode());
            }

            String responseBody = response.body();
            if (responseBody == null || responseBody.isEmpty()) return empty;

            ModrinthVersionSource[] updater = gson.fromJson(responseBody, ModrinthVersionSource[].class);
            if (updater.length == 0) return empty;

            for (ModrinthVersionSource source : updater) {
                if (!source.getGameVersions().contains(gameVersion)) continue;
                if (!source.getLoaders().contains(loader)) continue;
                if (simpleMode) {
                    if (!Objects.equals(source.getVersionNumber(), localVersion)) {
                        return new UpdateRecord(true, source.getVersionNumber(), source.getChangelog(), "https://modrinth.com/plugin/" + source.getProjectId() + "/version/" + source.getId());
                    }
                } else if (VersionComparator.cmpVer(localVersion, source.getVersionNumber()) < 0) {
                    return new UpdateRecord(true, source.getVersionNumber(), source.getChangelog(), "https://modrinth.com/plugin/" + source.getProjectId() + "/version/" + source.getId());
                }
            }
        } catch (InterruptedException e) {
            throw new IOException("Request interrupted", e);
        }

        return empty;
    }
}

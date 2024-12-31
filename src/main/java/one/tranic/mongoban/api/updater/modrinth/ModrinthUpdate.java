package one.tranic.mongoban.api.updater.modrinth;

import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.updater.UpdateRecord;
import one.tranic.mongoban.api.updater.VersionComparator;
import one.tranic.mongoban.api.updater.modrinth.source.Loaders;
import one.tranic.mongoban.api.updater.modrinth.source.ModrinthVersionSource;
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

    /**
     * A default empty {@link UpdateRecord} used when no updates are available.
     */
    final UpdateRecord empty = new UpdateRecord(false, "", "", "");

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
     * it is caught, and the exception stack trace is printed, and {@code null} may be passed to the consumer
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
                consumer.accept(null);
                e.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the latest update information for a Modrinth project based on the specified parameters.
     * This method sends an HTTP GET request to the Modrinth API to fetch version data for the project.
     * It filters the data based on game version, loader type, and version comparison logic.
     * <p>
     * If a newer version is found, it constructs and returns an {@link UpdateRecord} containing the update details.
     * If no update is available or the version data is invalid, an empty update record is returned.
     *
     * @return an {@link UpdateRecord} containing update information if an update is available;
     * an empty update record if no updates are found or the response is invalid.
     * @throws IOException if an error occurs during the HTTP request or response handling.
     */
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

            ModrinthVersionSource[] updater = MongoBanAPI.jsonParser.parse(responseBody, ModrinthVersionSource[].class);
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

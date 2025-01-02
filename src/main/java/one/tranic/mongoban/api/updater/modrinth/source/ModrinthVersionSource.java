package one.tranic.mongoban.api.updater.modrinth.source;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModrinthVersionSource {
    private String name;
    @SerializedName("version_number")
    private String versionNumber;
    private String changelog;
    private List<Dependency> dependencies;
    @SerializedName("game_versions")
    private List<String> gameVersions;
    @SerializedName("version_type")
    private String versionType;
    private List<String> loaders;
    private boolean featured;
    private String status;
    @SerializedName("requested_status")
    private String requestedStatus;
    private String id;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("author_id")
    private String authorId;
    @SerializedName("date_published")
    private String datePublished;
    private int downloads;
    @SerializedName("changelog_url")
    private String changelogUrl;
    private List<File> files;

    public ModrinthVersionSource() {
    }

    public String getName() {
        return name;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getChangelog() {
        return changelog;
    }

    public @Nullable List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<String> getGameVersions() {
        return gameVersions;
    }

    public String getVersionType() {
        return versionType;
    }

    public List<String> getLoaders() {
        return loaders;
    }

    public List<Loaders> getLoadersOf() {
        List<Loaders> list = new ArrayList<>(loaders.size());
        for (String loader : loaders) {
            list.add(Loaders.of(loader));
        }
        return list;
    }

    public boolean isFeatured() {
        return featured;
    }

    public Status getStatus() {
        return Status.of(status);
    }

    public @Nullable RequestedStatus getRequestedStatus() {
        return RequestedStatus.of(requestedStatus);
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public int getDownloads() {
        return downloads;
    }

    public @Nullable String getChangelogUrl() {
        return changelogUrl;
    }

    public List<File> getFiles() {
        return files;
    }

    public static class Dependency {
        @SerializedName("version_id")
        private String versionId;
        @SerializedName("project_id")
        private String projectId;
        @SerializedName("file_name")
        private String fileName;
        @SerializedName("dependency_type")
        private String dependencyType;

        public Dependency() {
        }

        public String getVersionId() {
            return versionId;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getDependencyType() {
            return dependencyType;
        }
    }

    public static class File {
        private Hashes hashes;
        private String url;
        private String filename;
        private boolean primary;
        private int size;
        @SerializedName("file_type")
        private String fileType;

        public File() {
        }

        public Hashes getHashes() {
            return hashes;
        }

        public String getUrl() {
            return url;
        }

        public String getFilename() {
            return filename;
        }

        public boolean isPrimary() {
            return primary;
        }

        public int getSize() {
            return size;
        }

        public @Nullable String getFileType() {
            return fileType;
        }
    }

    public static class Hashes {
        private String sha512;
        private String sha1;

        public Hashes() {
        }

        public String getSha512() {
            return sha512;
        }

        public String getSha1() {
            return sha1;
        }
    }

}
package one.tranic.mongoban.api.updater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for comparing semantic version strings.
 * Versions can include numeric components, and optional suffixes separated by "-" or "+".
 * The comparator handles numeric and non-numeric segments and returns the relative ordering.
 */
public class VersionComparator {

    /**
     * Splits a version string into its main components and suffix.
     * The main components are separated by dots (".") and any suffix is included after a dash ("-").
     * The suffix will be an empty string if not present in the input version string.
     *
     * @param version the version string to be split into components. It may contain a suffix separated by
     *                a dash ("-") and optionally include metadata after a plus sign ("+").
     * @return a list of strings where each element represents a segment of the main version, and the last
     * element is either the suffix or an empty string if no suffix is present.
     */
    private static List<String> splitVersion(String version) {
        String[] mainParts = version.split("-")[0].split("\\+")[0].split("\\.");
        String suffix = version.contains("-") ? version.substring(version.indexOf('-') + 1) : "";
        List<String> result = new ArrayList<>(Arrays.asList(mainParts));
        result.add(suffix);
        return result;
    }

    /**
     * Compares two version strings based on semantic versioning rules.
     * The method handles numeric and non-numeric segments, as well as potential suffixes like
     * pre-release or build metadata identifiers.
     *
     * @param vLocal  the local version string to compare.
     * @param vRemote the remote version string to compare against.
     * @return a negative integer if vLocal is less than vRemote, a positive integer if vLocal
     * is greater than vRemote, or zero if both versions are equal.
     */
    public static int cmpVer(String vLocal, String vRemote) {
        List<String> vLocSeg = splitVersion(vLocal);
        List<String> vRemSeg = splitVersion(vRemote);

        int maxLen = Math.max(vLocSeg.size(), vRemSeg.size());

        for (int i = 0; i < maxLen; i++) {
            String localPart = i < vLocSeg.size() ? vLocSeg.get(i) : "0";
            String remotePart = i < vRemSeg.size() ? vRemSeg.get(i) : "0";

            int cmpResult;

            if (Pattern.matches("\\d+", localPart) && Pattern.matches("\\d+", remotePart)) {
                cmpResult = Integer.compare(Integer.parseInt(localPart), Integer.parseInt(remotePart));
            } else if (Pattern.matches("\\d+", localPart)) {
                cmpResult = 1;
            } else if (Pattern.matches("\\d+", remotePart)) {
                cmpResult = -1;
            } else {
                cmpResult = localPart.compareToIgnoreCase(remotePart);
            }

            if (cmpResult != 0) {
                return cmpResult;
            }
        }

        return 0;
    }
}

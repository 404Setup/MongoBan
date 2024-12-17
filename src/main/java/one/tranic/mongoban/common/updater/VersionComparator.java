package one.tranic.mongoban.common.updater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class VersionComparator {

    private static List<String> splitVersion(String version) {
        String[] mainParts = version.split("-")[0].split("\\+")[0].split("\\.");
        String suffix = version.contains("-") ? version.substring(version.indexOf('-') + 1) : "";
        List<String> result = new ArrayList<>(Arrays.asList(mainParts));
        result.add(suffix);
        return result;
    }

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

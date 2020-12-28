package ru.nsu.fit.towerdefense.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class UserMetaData {

    private static final String RESEARCH_POINTS_KEY = "researchPoints";

    private static final Preferences PREFERENCES = Preferences.userRoot().node(UserMetaData.class.getName());

    public static int getResearchPoints() {
        return PREFERENCES.getInt(RESEARCH_POINTS_KEY, 0);
    }

    public static void setResearchPoints(int researchPoints) {
        PREFERENCES.putInt(RESEARCH_POINTS_KEY, researchPoints);
    }

    public static void addResearchPoints(int researchPoints) {
        PREFERENCES.putInt(RESEARCH_POINTS_KEY, getResearchPoints() + researchPoints);
    }

    public static List<String> getUnlockedResearchNames(List<String> availableResearchNames) {
        List<String> unlockedResearchNames = new ArrayList<>();

        for (String availableResearchName : availableResearchNames) {
            if (PREFERENCES.getBoolean(availableResearchName, false)) {
                unlockedResearchNames.add(availableResearchName);
            }
        }

        return unlockedResearchNames;
    }

    public static boolean isResearched(String researchName) {
        return PREFERENCES.getBoolean(researchName, false);
    }

    public static void saveResearch(String researchName) {
        PREFERENCES.putBoolean(researchName, true);
    }
}

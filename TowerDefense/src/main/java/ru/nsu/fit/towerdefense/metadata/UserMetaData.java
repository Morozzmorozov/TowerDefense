package ru.nsu.fit.towerdefense.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class UserMetaData {

    private static final String RESEARCH_POINTS_KEY = "researchPoints";
    private static final String MULTIPLAYER_POINTS_KEY = "multiplayerPoints";

    private static final Preferences PREFERENCES = Preferences.userRoot().node(UserMetaData.class.getName());

    public static int getResearchPoints() {
        return PREFERENCES.getInt(RESEARCH_POINTS_KEY, 0);
    }

    public static void setResearchPoints(int researchPoints) {
        PREFERENCES.putInt(RESEARCH_POINTS_KEY, researchPoints);
    }

    public static void subtractResearchPoints(int researchPoints) {
        PREFERENCES.putInt(RESEARCH_POINTS_KEY, getResearchPoints() - researchPoints);
    }

    public static void addResearchPoints(int researchPoints) {
        PREFERENCES.putInt(RESEARCH_POINTS_KEY, getResearchPoints() + researchPoints);
    }

    public static int getMultiplayerPoints() {
        return PREFERENCES.getInt(MULTIPLAYER_POINTS_KEY, 0);
    }

    public static void setMultiplayerPoints(int multiplayerPoints) {
        PREFERENCES.putInt(MULTIPLAYER_POINTS_KEY, multiplayerPoints);
    }

    public static void subtractMultiplayerPoints(int multiplayerPoints) {
        PREFERENCES.putInt(MULTIPLAYER_POINTS_KEY, getMultiplayerPoints() - multiplayerPoints);
    }

    public static void addMultiplayerPoints(int multiplayerPoints) {
        PREFERENCES.putInt(MULTIPLAYER_POINTS_KEY, getMultiplayerPoints() + multiplayerPoints);
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

    // ----- Debug -----

    public static void clear() {
        try {
            PREFERENCES.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}

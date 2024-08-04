package utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MementoState {
    private static final String STATE_SAVE_PATH = SaveManager.getSaveFilePath("MementoState.autosave");

    private static Gson gson = new Gson();

    public static Map<String, String> getState() {
        String json = SaveManager.readSave(STATE_SAVE_PATH);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> stateMap = gson.fromJson(json, type);
        if (stateMap == null) {
            stateMap = new HashMap<>();
        }
        return stateMap;
    }

    public static void saveState(Map<String, String> stateMap) {
        String json = gson.toJson(stateMap);
        SaveManager.writeSave(STATE_SAVE_PATH, json);
    }

    public static String getStateVariable(String key) {
        Map<String, String> stateMap = getState();
        return stateMap.getOrDefault(key, "");
    }

    public static void saveStateVariable(String key, String value) {
        Map<String, String> stateMap = getState();
        stateMap.put(key, value);
        saveState(stateMap);
    }
}

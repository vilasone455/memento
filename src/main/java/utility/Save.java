package utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Save {
        public static String getSave() {
        try {
            if (Gdx.files.absolute(getUnlockRelicSavePath()).exists()) {
                String savestr = loadSaveString(getUnlockRelicSavePath());
                try {
                    return savestr;
                } catch (Exception e) {
                    return "";
                }
            }
        } catch (JsonSyntaxException e) {
            return "";
        }
        return "";
    }

    public static ArrayList<String> getUnlockedRelics(){
        String relicStrs = getSave();
        String[] splitArray = relicStrs.split("\\|");
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(splitArray));
        return arrayList;
    }

    public static void addRelicAndSave(ArrayList<String> relics , String addRelic){
        relics.add(addRelic);
        String relicStr = convertArrayListToString(relics);
        try {
            Files.write(Paths.get(getUnlockRelicSavePath()), relicStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveRelic(String relics){
        try {
            Files.write(Paths.get(getUnlockRelicSavePath()), relics.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String convertArrayListToString(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() != 0) {
                sb.append("|");
            }
            sb.append(s);
        }

        return sb.toString();
    }


    public static void deleteSave() {
        Gdx.files.absolute(getUnlockRelicSavePath()).delete();
    }

    private static String loadSaveString(String filePath) {
        FileHandle file = Gdx.files.absolute(filePath);
        String data = file.readString();
        return data;
    }

    private static String getUnlockRelicSavePath() {
        return ConfigUtils.CONFIG_DIR + File.separator + "Memento" + ".autosave" ;
    }

}

package utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.File;

public class SaveManager {

    private static final String DELIMITER = "|";

    public static String readSave(String filePath) {
        try {
            FileHandle file = Gdx.files.absolute(filePath);
            return file.readString();
        } catch (Exception e) {
            handleException(e, "Error reading save file");
            return "";
        }
    }

    public static void writeSave(String filePath, String data) {
        try {
            Files.write(Paths.get(filePath), data.getBytes());
        } catch (IOException e) {
            handleException(e, "Error writing save file");
        }
    }

    public static <T> ArrayList<T> parseData(String data, DataParser<T> parser) {
        String[] splitArray = data.split("\\|");

        ArrayList<T> list = new ArrayList<>();
        for (String item : splitArray) {
            list.add(parser.parse(item));
        }
        return list;
    }

    public static String convertListToString(ArrayList<String> list) {
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

    public static void deleteSave(String filePath) {
        Gdx.files.absolute(filePath).delete();
    }

    static String getSaveFilePath(String filename) {
        return ConfigUtils.CONFIG_DIR + File.separator + filename;
    }

    private static void handleException(Exception e, String message) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    @FunctionalInterface
    public interface DataParser<T> {
        T parse(String data);
    }

    @FunctionalInterface
    public interface DataFormatter<T> {
        String format(T data);
    }
}

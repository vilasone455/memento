package utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

public class Relic {
    public static ArrayList<String> getRelicUnlockeds() {
        String relicStrs = "";
        SpireConfig config = null;
        try {
            config = new SpireConfig("Memento", "Progression");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (config.has("relics")) {
            relicStrs = config.getString("relics");
		}

        String[] splitArray = relicStrs.split("\\|");

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(splitArray));

        return arrayList;
    }
}

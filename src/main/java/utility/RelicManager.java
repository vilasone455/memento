package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class RelicManager {

    private static final String RELIC_SAVE_PATH = SaveManager.getSaveFilePath("Memento.autosave");

    public static ArrayList<String> getUnlockedRelics() {
        String relicData = SaveManager.readSave(RELIC_SAVE_PATH);
        System.out.println("hhh");
        System.out.println(relicData);

        return SaveManager.parseData(relicData, String::new);
    }

    public static void addRelicAndSave(ArrayList<String> relics, String newRelic) {
        relics.add(newRelic);
        String relicData = SaveManager.convertListToString(relics);
        SaveManager.writeSave(RELIC_SAVE_PATH, relicData);
    }

    public static void deleteSave() {
        SaveManager.deleteSave(RELIC_SAVE_PATH);
    }

    public static void addTwoItem() {
        ArrayList<String> curRelics = RelicManager.getUnlockedRelics();
        ArrayList<String> poolRelics = new ArrayList<>();
        ArrayList<AbstractRelic> rareRelics = RelicLibrary.rareList;
        ArrayList<AbstractRelic> uncommonRelic = RelicLibrary.uncommonList;
        for (AbstractRelic r : rareRelics) {
            poolRelics.add(r.relicId);
        }
        for (AbstractRelic r : uncommonRelic) {
            poolRelics.add(r.relicId);
        }
        ArrayList<String> res = addRandomItems(curRelics, poolRelics, 2);
        String relicData = SaveManager.convertListToString(res);
        SaveManager.writeSave(RELIC_SAVE_PATH, relicData);
        System.out.println(relicData);
    }

    public static ArrayList<String> addRandomItems(ArrayList<String> currentItems, ArrayList<String> itemPool, int numberOfItemsToAdd) {
        // Use a set for faster lookups to check for duplicates
        Set<String> currentItemsSet = new HashSet<>(currentItems);
        ArrayList<String> availableItems = new ArrayList<>();

        // Filter out items in itemPool that are already in currentItems
        for (String item : itemPool) {
            if (!currentItemsSet.contains(item)) {
                availableItems.add(item);
            }
        }

        // Shuffle the availableItems list
        Collections.shuffle(availableItems);

        // Add items to currentItems, ensuring no duplicates
        for (int i = 0; i < numberOfItemsToAdd && i < availableItems.size(); i++) {
            currentItems.add(availableItems.get(i));
        }

        return currentItems;
    }

}

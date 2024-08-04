package patches;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

import relics.Mementos;
import rewards.ShopRelicReward;
import utility.MementoState;
import vfx.ObtainRelicLater;

public class NeowEventPatches {

	private static String optionName = "[Get Currently Selected Relic (Punishment based on relic rarity)]";
	private static String option2Name = "[Get Earn Later Selected Relic (Punishment based on relic rarity)]";
	private static String option3Name = "[Select Relic and Buy Later]";
	
	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowRoom.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class AddBetterRewardsButton {
		@SpirePostfixPatch
		public static void Postfix(NeowRoom room, boolean b) {
			room.event.roomEventText.addDialogOption(optionName);
			room.event.roomEventText.addDialogOption(option2Name);
			room.event.roomEventText.addDialogOption(option3Name);

		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class FixEventImage {
		@SpirePostfixPatch
		public static void Postfix(NeowEvent e, boolean b) {
			e.imageEventText.clear();
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = "buttonEffect")
	public static class MaybeStartRewards {
		@SpirePrefixPatch
		public static void Prefix(AbstractEvent e, int buttonPressed) {
			try {
				Field screenNumField = NeowEvent.class.getDeclaredField("screenNum");
				screenNumField.setAccessible(true);
				int sn = screenNumField.getInt(e);
				maybeStartRewards(e, buttonPressed, screenNumField, sn);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@SpirePatch(cls = "downfall.events.HeartEvent", method = "buttonEffect", optional = true)
	public static class MaybeStartRewardsDownfall {
		@SpirePrefixPatch
		public static void Prefix(AbstractEvent e, int buttonPressed) {
			try {
				Class<? extends AbstractEvent> heartEventClass = Class.forName("downfall.events.HeartEvent")
						.asSubclass(AbstractEvent.class);
				Field screenNumField = heartEventClass.getDeclaredField("screenNum");
				screenNumField.setAccessible(true);
				int sn = screenNumField.getInt(e);
				maybeStartRewards(e, buttonPressed, screenNumField, sn);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private static void maybeStartRewards(AbstractEvent e, int buttonPressed, Field screenNumField, int sn)
			throws IllegalAccessException {

		ArrayList<LargeDialogOptionButton> optionList = e.roomEventText.optionList;
	    Map<String, Integer> optionIndices = new HashMap<>();
    optionIndices.put(optionName, -1);
    optionIndices.put(option2Name, -1);
    optionIndices.put(option3Name, -1);

    for (int i = 0; i < optionList.size(); i++) {
        LargeDialogOptionButton op = optionList.get(i);
        if (optionIndices.containsKey(op.msg)) {
            optionIndices.put(op.msg, i);
        }
    }

    Integer optionIndex1 = optionIndices.get(optionName);
    Integer optionIndex2 = optionIndices.get(option2Name);
    Integer optionIndex3 = optionIndices.get(option3Name);

    if (sn == 1 && (buttonPressed == optionIndex1 || buttonPressed == optionIndex2 || buttonPressed == optionIndex3)) {
        String mementoType = null;
        if (buttonPressed == optionIndex1) {
            mementoType = "ObtainInstant";
        } else if (buttonPressed == optionIndex2) {
            mementoType = "ObtainLater";
        } else if (buttonPressed == optionIndex3) {
            mementoType = "ObtainByShop";
        }

        if (mementoType != null) {

            MementoState.saveStateVariable("mementoType", mementoType);
            AbstractRelic targetRelic = RelicLibrary.getRelic("memento:mementos").makeCopy();
			if (buttonPressed == optionIndex3) {
			((Mementos) targetRelic).selectItem = 2;

			}
            AbstractDungeon.effectsQueue.add(0, new ObtainRelicLater(targetRelic));
        }
    }

	}
}
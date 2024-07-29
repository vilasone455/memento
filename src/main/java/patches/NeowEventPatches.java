package patches;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

import vfx.ObtainRelicLater;

public class NeowEventPatches {

	private static String optionName = "[Get Currently Selected Relic (Punishment based on relic rarity)]";

	
	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowRoom.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class AddBetterRewardsButton {
		@SpirePostfixPatch
		public static void Postfix(NeowRoom room, boolean b) {
			room.event.roomEventText.addDialogOption(optionName);
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

	// screenNum = 0, 1 or 2 mean talk option
	// 10 is only ok for trial (Custom Mode) I think
	private static boolean acceptableScreenNum(int sn) {
		return sn == 0 || sn == 1 || sn == 2 || (Settings.isTrial && sn == 10);
	}

	private static void maybeStartRewards(AbstractEvent e, int buttonPressed, Field screenNumField, int sn)
			throws IllegalAccessException {
		System.out.println("button pass." + buttonPressed + " screen num " + screenNumField + " sn " + sn);

		ArrayList<LargeDialogOptionButton> optionList = e.roomEventText.optionList;
		Integer index = -1;
		
		for (int i = 0; i < optionList.size(); i++) {
			LargeDialogOptionButton op = optionList.get(i);
			System.out.println("message " + op.msg);
			if(op.msg.equals(optionName)){
				index = i;
			}

		}

		if (index != -1 && buttonPressed == index && sn == 1) {

			// SpireConfig config = null;
			// try {
			// 	config = new SpireConfig("Memento", "Config");
			// } catch (IOException e1) {
			// 	e1.printStackTrace();
			// }
			// String relic = "";
			// if (config.has("curRelic")) {
			// 	relic = config.getString("curRelic");
			// }
			AbstractRelic targetRelic = RelicLibrary.getRelic("memento:mementos").makeCopy();
			// if (targetRelic.tier == RelicTier.RARE) {
			// 	AbstractCard curse = new CurseOfTheBell();
			// 	AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
			// 			Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

			// } else if (targetRelic.tier == RelicTier.BOSS) {
			// 	AbstractDungeon.player.decreaseMaxHealth(12);
			// 	AbstractCard curse = new CurseOfTheBell();
			// 	AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
			// 			Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			// }
			AbstractDungeon.effectsQueue.add(0, new ObtainRelicLater(targetRelic));
			// AbstractDungeon.getCurrRoom().event.imageEventText.clearAllDialogs();
			// AbstractDungeon.getCurrRoom().event.imageEventText.clearRemainingOptions();

			// screenNum = 99 is the default value for leave event. This
			// calls openMap, which is patched to start a BetterRewards

		} else {
		}
	}
}
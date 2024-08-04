package patches;


import java.util.ArrayList;
import java.util.Map;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BagOfPreparation;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;

import relics.Mementos;
import rewards.ShopRelicReward;
import utility.MementoState;
import utility.SaveManager;


public class RewardModifierPatches {
    	@SpirePatch(clz = com.megacrit.cardcrawl.screens.CombatRewardScreen.class, method = "setupItemReward")
	public static class FixEventImage {
		@SpirePostfixPatch
		public static void Postfix(CombatRewardScreen __instance) {
			// e.imageEventText.clear();
			ArrayList<AbstractRelic> curRelics = AbstractDungeon.player.relics;

			for (AbstractRelic re : curRelics) {
				if(re.relicId == "memento:mementos"){
					ArrayList<String> items = ((Mementos) re).selectItems;
					System.out.println("count re " + items.size());
				}
			}

			Map<String, String> state = MementoState.getState();
			String mementoType = state.get("mementoType");
			if(mementoType.equals("ObtainByShop")){
				ArrayList<String> curRelicStrs = new ArrayList<>();
				for (AbstractRelic rel : curRelics) {
					curRelicStrs.add(rel.relicId);
				}
				String relicName = state.get("selectRelic");
				if(!curRelicStrs.contains(relicName)){
					RewardItem rewardItem = new ShopRelicReward(relicName);
					__instance.rewards.add(rewardItem);
				}
				
			}


		}
	}

	


}
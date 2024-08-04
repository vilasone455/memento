package rewards;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.badlogic.gdx.graphics.Color;

import basemod.abstracts.CustomReward;

public class ShopRelicReward extends CustomReward {

   public String curRelic;
   public AbstractRelic curRelicItem;
   public static int price = 25;

   public static float REWARD_TEXT_X = (float) Settings.WIDTH * 0.434F;

   public ShopRelicReward(String relic) {
      this(RelicLibrary.getRelic(relic));
   }

   public ShopRelicReward(AbstractRelic relic) {
      super(ImageMaster.UI_GOLD, relic.name+ " (" + price + " gold)", CustomRewardTypes.SHOP_RELIC);
      this.curRelicItem = relic;
   }

   @Override
   public boolean claimReward() {
      int gold = AbstractDungeon.player.gold;
      if(gold >= this.curRelicItem.cost){
         AbstractDungeon.player.loseGold(price);
         this.curRelicItem.instantObtain();
         CardCrawlGame.metricData.addRelicObtainData(this.curRelicItem);
         return true;
      }
      return false;
   }


}
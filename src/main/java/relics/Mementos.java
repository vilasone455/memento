package relics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Clumsy;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;

import relics.abstracts.MementoRelic;
import utility.Relic;
import utility.RelicSelectScreen;
import vfx.ObtainRelicLater;
import com.megacrit.cardcrawl.core.Settings;

public class Mementos extends MementoRelic {
    public static final String ID = "memento:mementos";
    private boolean relicSelected = true;
    private RelicSelectScreen relicSelectScreen;
    private boolean fakeHover = false;

    public Mementos() {
        // super(ID, TexLoader.getTexture(makeRelicPath(setId.replace(modID + ":", "") +
        // ".png")), tier, sfx);
        super(ID, "mementos.png", RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0]; // DESCRIPTIONS pulls from your localization file
    }

    @Override
    public void onEquip() {
        // int count = 0;
        // for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
        // if (c.isEthereal) { // when equipped (picked up) this relic counts how many
        // ethereal cards are in the player's deck
        // count++;
        // }
        // }
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        openRelicSelect();
    }

    private void openRelicSelect() {
        relicSelected = false;

        ArrayList<AbstractRelic> relics = new ArrayList<>();
        ArrayList<String> relicUnlockeds = Relic.getRelicUnlockeds();

        for (String r : relicUnlockeds) {
            System.out.println("relic name is : " + r);
            AbstractRelic relic = RelicLibrary.getRelic(r);
            relics.add(relic);
        }

        relicSelectScreen = new RelicSelectScreen();
        relicSelectScreen.open(relics);
    }

    @Override
    public void update() {
        super.update();

        if (!relicSelected) {
            if (relicSelectScreen.doneSelecting()) {
                relicSelected = true;

                AbstractRelic libraryRelic = relicSelectScreen.getSelectedRelics().get(0);
                float x = libraryRelic.currentX;
                float y = libraryRelic.currentY;
                AbstractRelic relic = libraryRelic.makeCopy();
                switch (relic.tier) {
                    case COMMON:
                        AbstractDungeon.commonRelicPool.removeIf(id -> id.equals(relic.relicId));
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonRelicPool.removeIf(id -> id.equals(relic.relicId));
                        break;
                    case RARE:
                        AbstractDungeon.rareRelicPool.removeIf(id -> id.equals(relic.relicId));
                        break;
                    case SHOP:
                        AbstractDungeon.shopRelicPool.removeIf(id -> id.equals(relic.relicId));
                        break;
                    case BOSS:
                        AbstractDungeon.bossRelicPool.removeIf(id -> id.equals(relic.relicId));
                        break;
                }
                
                AbstractDungeon.effectsQueue.add(0, new ObtainRelicLater(relic));
                if(relic.tier == RelicTier.COMMON){
                    AbstractDungeon.player.loseGold(70);
                }
                else if (relic.tier == RelicTier.UNCOMMON){
                    AbstractDungeon.player.loseGold(100);;
                    AbstractCard clumsy = new Clumsy();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(clumsy.makeStatEquivalentCopy(),
                    Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                } if (relic.tier == RelicTier.RARE) {
                    AbstractCard curse = new CurseOfTheBell();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                            
                    AbstractDungeon.player.loseGold(100);;

                } else if (relic.tier == RelicTier.BOSS) {
                    AbstractDungeon.player.decreaseMaxHealth(12);
                    AbstractCard curse = new CurseOfTheBell();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractDungeon.player.loseGold(100);;

                } else if(relic.tier == RelicTier.SHOP){
                    AbstractCard curse = new CurseOfTheBell();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractDungeon.player.loseGold(100);;

                }

                // AbstractDungeon.effectsQueue.add(0, new ObtainRelicLater(relic, x, y));

                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            } else {
                relicSelectScreen.update();
                if (!hb.hovered) {
                    fakeHover = true;
                }
                hb.hovered = true;
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        if (!relicSelected && fakeHover) {
            relicSelectScreen.render(sb);
        }
        if (fakeHover) {
            fakeHover = false;
            hb.hovered = false;
        } else {
            super.renderTip(sb);
        }
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        super.renderInTopPanel(sb);

        if (!relicSelected && !fakeHover) {
            relicSelectScreen.render(sb);
        }
    }

    @Override
    public AbstractRelic makeCopy() { // always override this method to return a new instance of your relic
        return new Mementos();
    }

}
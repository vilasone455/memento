package relics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.Sys;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.abstracts.CustomSavable;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Clumsy;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;

import relics.abstracts.MementoRelic;
import utility.MementoState;
import utility.RelicManager;
import utility.RelicSelectScreen;
import vfx.ObtainRelicLater;
import com.megacrit.cardcrawl.core.Settings;

public class Mementos extends MementoRelic implements ClickableRelic, CustomSavable<ArrayList<String>>{
    public static final String ID = "memento:mementos";
    private boolean relicSelected = true;
    private RelicSelectScreen relicSelectScreen;
    private boolean fakeHover = false;
    private boolean obtainRelicLater = false;
    private String currentSelectRelic = "";
    public int selectItem = 1;
    private String type = "";
    public ArrayList<String> selectItems = new ArrayList<>();


    public Mementos() {
        super(ID, "mementos.png", RelicTier.SPECIAL, LandingSound.HEAVY);
        Map<String, String> state = MementoState.getState();
        this.type = state.get("mementoType");
        this.currentSelectRelic = state.get("selectRelic");
        System.out.println("cur relic type "  + type);

        System.out.println("initttt relic type "  + currentSelectRelic);

    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0]; // DESCRIPTIONS pulls from your localization file

        // if(type.equals("ObtainInstant")){
        //     return DESCRIPTIONS[0]; // DESCRIPTIONS pulls from your localization file
        // }else if(type.equals("ObtainLater")){
        //     AbstractRelic targetRelic = RelicLibrary.getRelic(currentSelectRelic);
        //     return DESCRIPTIONS[1] + targetRelic.name + " ."; // DESCRIPTIONS pulls from your localization file
        // }
        // return DESCRIPTIONS[0]; // DESCRIPTIONS pulls from your localization file
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
        selectItems.add("Hello world");
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
        ArrayList<String> relicUnlockeds = RelicManager.getUnlockedRelics();
        Set<String> addedRelics = new HashSet<>();

        for (String r : relicUnlockeds) {
            System.out.println("relic name is : " + r);
            if (!addedRelics.contains(r)) {
                AbstractRelic relic = RelicLibrary.getRelic(r);
                relics.add(relic);
                addedRelics.add(r); // Mark this relic as added
            }

        }
        

        // relics.addAll(RelicLibrary.commonList);
        // relics.addAll(RelicLibrary.uncommonList);

        relicSelectScreen = new RelicSelectScreen();
        relicSelectScreen.setSelectCount(selectItem);
        relicSelectScreen.open(relics);
    }

    @Override
    public void update() {
        super.update();

        if (!relicSelected) {
            if (relicSelectScreen.doneSelecting()) {
                relicSelected = true;
                AbstractRelic libraryRelic = relicSelectScreen.getSelectedRelics().get(0);
                System.out.println("type " + this.type);
                if (this.type.equals("ObtainInstant")) {
                    // currentSelectRelic = libraryRelic.relicId;
                    obtainRelic(libraryRelic);
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                } else if (this.type.equals("ObtainLater")) {
                    currentSelectRelic = libraryRelic.relicId;
                    MementoState.saveStateVariable("selectRelic", libraryRelic.relicId);
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                } else if(this.type.equals("ObtainByShop")){
                    currentSelectRelic = libraryRelic.relicId;
                    MementoState.saveStateVariable("selectRelic", libraryRelic.relicId);
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;

                }

            } else {
                relicSelectScreen.update();
                if (!hb.hovered) {
                    fakeHover = true;
                }
                hb.hovered = true;
            }
        }
    }

    private void obtainRelic(AbstractRelic selectRelic) {
        // float x = libraryRelic.currentX;
        // float y = libraryRelic.currentY;
        AbstractRelic relic = selectRelic.makeCopy();
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
        if (relic.tier == RelicTier.COMMON) {
            AbstractDungeon.player.loseGold(70);
        } else if (relic.tier == RelicTier.UNCOMMON) {
            AbstractDungeon.player.loseGold(100);
            ;
            AbstractCard clumsy = new Clumsy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(clumsy.makeStatEquivalentCopy(),
                    Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

        }
        if (relic.tier == RelicTier.RARE) {
            AbstractCard curse = new CurseOfTheBell();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                    Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

            AbstractDungeon.player.loseGold(100);
            ;

        } else if (relic.tier == RelicTier.BOSS) {
            AbstractDungeon.player.decreaseMaxHealth(12);
            AbstractCard curse = new CurseOfTheBell();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                    Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.loseGold(100);
            ;

        } else if (relic.tier == RelicTier.SHOP) {
            AbstractCard curse = new CurseOfTheBell();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse.makeStatEquivalentCopy(),
                    Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.loseGold(100);
            ;

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

    @Override
    public void onRightClick() {
        if(type.equals("ObtainLater")){
            AbstractRelic targetRelic = RelicLibrary.getRelic(currentSelectRelic);
            obtainRelic(targetRelic);
            Map<String , String> state = new HashMap<>();

            state.put("mementoType", "ObtainInstant");
            state.put("selectRelic", "");
            MementoState.saveState(state);
        }
    }

    @Override
    public void onLoad(ArrayList<String> arg0) {
        this.selectItems = new ArrayList<>(arg0);
    }

    @Override
    public ArrayList<String> onSave() {
        return selectItems;
    }


}
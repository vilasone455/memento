
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import basemod.ModLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;

import basemod.BaseMod;
import basemod.helpers.RelicType;

import basemod.IUIElement;
import basemod.ModPanel;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostCreateStartingRelicsSubscriber;
import basemod.interfaces.PostDeathSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
// import relics.Mementos;
import relics.Mementos;
import rewards.CustomRewardTypes;
import rewards.ShopRelicReward;
import utility.MementoState;
import utility.RelicManager;
import utility.Save;

import java.util.Random;


@SpireInitializer // this annotation tells ModTheSpire to look at this class to initialize our mod
public class Memento implements PostCreateStartingRelicsSubscriber, PostInitializeSubscriber, PostBattleSubscriber,
        PostDeathSubscriber , PostDungeonInitializeSubscriber , EditStringsSubscriber
        , EditRelicsSubscriber
          {

    public static final Logger logger = LogManager.getLogger(Memento.class.getName()); // lets us log output

    public static final String MODNAME = "Memento"; // mod name
    public static final String AUTHOR = "You"; // your name
    public static final String DESCRIPTION = "meta progression"; // description (w/ version # if you want)
    private static final float DROPDOWN_X = 400f;
    private static final float DROPDOWN_Y = 600f;
    private static SpireConfig metaProgressions = null;
    private static SpireConfig config = null;
    public DropdownMenu relicSelectDropdown = null;

    public Memento() {
        logger.info("subscribing to PostCreateStartingRelics and postInitialize events");
        BaseMod.subscribe(this);
    }

    private String convertArrayListToString(ArrayList<String> list) {
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

    public static String assetPath(String path) {
        return "mementoAssets/" + path;
    }

    public static String relicPath(String resourcePath) {
        return "mementoAssets/images/relics/" + resourcePath;
    }

    private static String makeLocPath(String filename)
    {
        String toReturn = "localization/";
        switch (Settings.language)
        {
            case RUS:
                toReturn += "rus/";
                break;
            case ZHS:
                toReturn += "zhs/";
                break;
            default:
                toReturn += "eng/";
                break;
        }
        return (toReturn + filename + ".json");
    }


    @Override
    public void receiveEditStrings(){
        BaseMod.loadCustomStringsFile(RelicStrings.class, assetPath(makeLocPath("Memento-RelicStrings")));

    }


    @Override
    public void receiveEditRelics()
    {
        BaseMod.addRelic(new Mementos(), RelicType.SHARED);

    	// BaseMod.addRelic(new Mementos(), RelicType.SHARED);
    }

    

    private ArrayList<String> getRelicUnlockeds() {
        String relicStrs = "";
        if (metaProgressions.has("relics")) {
            relicStrs = metaProgressions.getString("relics");
        } else {
            relicStrs = "Anchor";
        }
        ;
        String[] splitArray = relicStrs.split("\\|");

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(splitArray));

        return arrayList;
    }

    public static void initialize() { // ModTheSpire will call this method to initialize because of the annotation we
        @SuppressWarnings("unused")
        Memento mod = new Memento();
        try {
            config = new SpireConfig("Memento", "Config");
            config.setBool("obtainRelicLater", false);
            metaProgressions = new SpireConfig("Memento", "Progression");
            config.save();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receivePostInitialize() {


        ModPanel settingsPanel = new ModPanel();


        ModLabel label = new ModLabel("My mod does not have any settings (yet)!", 400.0f, 700.0f, settingsPanel,
                (me) -> {
                });


        // String curRelic = "Anchor";
        settingsPanel.addUIElement(label);
        // if(config.has("curRelic")){
        //     System.out.println("Pass .");

        //     curRelic = config.getString("curRelic");
        //     System.out.println("That's ." + curRelic);

        // }

        // String relicStrs = "";
        // if (metaProgressions.has("relics")) {
        //     relicStrs = metaProgressions.getString("relics");
        // } else {
        //     relicStrs = "Anchor";
        // }
        // Save.saveString(relicStrs);
        ArrayList<String> relicUnlockeds = RelicManager.getUnlockedRelics();

        // Integer curIndex = 0;

        // for (int i = 0; i < relicUnlockeds.size(); i++) {
        //     String item = relicUnlockeds.get(i);

        //     if(curRelic.equals(item)){
        //         curIndex = i;
        //         break;
        //     }
        // }

        relicSelectDropdown = new DropdownMenu((dropdownMenu, index, s) -> {
            // config.setString("curRelic", s);
            // try {
            //     config.save();
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
        }, relicUnlockeds, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        // relicSelectDropdown.setSelectedIndex(curIndex);
        IUIElement wrapperDropdown = new IUIElement() {
            public void render(SpriteBatch sb) {
                relicSelectDropdown.render(sb, DROPDOWN_X * Settings.xScale, DROPDOWN_Y * Settings.yScale);
            }

            public void update() {
                relicSelectDropdown.update();
            }

            public int renderLayer() {
                return 3;
            }

            public int updateOrder() {
                return 0;
            }
        };
        settingsPanel.addUIElement(wrapperDropdown);

        BaseMod.registerModBadge(ImageMaster.loadImage(assetPath("images/memento/modBadge.png")), "Memento", "Top",
                "TODO", settingsPanel);

                // RelicManager.addTwoItem();
        this.registerCustomRewards();
    }

    @Override
    public void receivePostCreateStartingRelics(PlayerClass playerClass, ArrayList<String> relicsToAdd) {

    }

    @Override
    public void receivePostDeath() {

        if (AbstractDungeon.actNum == 3) {
            rewardRelic();
        }
    }

    private void rewardRelic(){
        ArrayList<AbstractRelic> relics = AbstractDungeon.player.relics;
        ArrayList<String> relicPools = new ArrayList<>();
        ArrayList<String> relicUnlockeds = getRelicUnlockeds();

        for (int i = 0; i < relics.size(); i++) {
            AbstractRelic relic = relics.get(i);
            if (relic.tier != RelicTier.STARTER && !relicUnlockeds.contains(relic.relicId)) {
                relicPools.add(relic.relicId);
            }
        }
        if (!relicPools.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(relicPools.size());
            String selectedRelicId = relicPools.get(randomIndex);
            relicUnlockeds.add(selectedRelicId);
            Save.addRelicAndSave(relicUnlockeds, selectedRelicId);
            // metaProgressions.setString("relics", convertArrayListToString(relicUnlockeds));
            // try {
            //     metaProgressions.save();
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }

            System.out.println("Randomly selected relic ID: " + selectedRelicId);
        } else {
            System.out.println("No non-starter relics found.");
        }

    }

    @Override
    public void receivePostBattle(AbstractRoom arg0) {
        if(AbstractDungeon.floorNum == 50){
            System.out.println("Win act 3 " );
            rewardRelic();
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        MementoState.saveStateVariable("mementoType", "ObtainInstant");

            // addDialogOption(((NeowReward)this.rewards.get(3)).optionLabel);

        // TODO Auto-generated method stub
        	// NeowEvent neowEvent = new NeowEvent();
            // neowEvent.roomEventText.addDialogOption("5 Option");
			// Method method = null;
			// try {
			// 	method = neowEvent.getClass().getDeclaredMethod("blessing");
			// } catch (NoSuchMethodException | SecurityException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			
			
			// method.setAccessible(true);

			// try {
			// 	method.invoke(neowEvent);
			// } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// 	e.printStackTrace();
			// }

    }

        private void registerCustomRewards() {
        
        BaseMod.registerCustomReward(
            CustomRewardTypes.SHOP_RELIC, 
            (rewardSave) -> { // this handles what to do when this quest type is loaded.
                return new ShopRelicReward(rewardSave.id);
            }, 
            (customReward) -> { // this handles what to do when this quest type is saved.
                String relicId = ((ShopRelicReward) customReward).curRelic;

                return new RewardSave(customReward.type.toString(), relicId);
            });


    }



}

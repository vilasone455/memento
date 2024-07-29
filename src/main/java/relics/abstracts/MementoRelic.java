
package relics.abstracts;

import com.evacipated.cardcrawl.mod.stslib.relics.SuperRareRelic;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import utility.Path;

public abstract class MementoRelic extends AbstractRelic
{
    public MementoRelic(String setId, String imgName, RelicTier tier, LandingSound sfx)
    {
        super(setId, "", tier, sfx);

        if (this instanceof SuperRareRelic) {
            this.tier = tier = RelicTier.RARE;
        }

        imgUrl = imgName;

        if (img == null || outlineImg == null) {
            
            img = ImageMaster.loadImage(Path.relicPath(imgName));

            outlineImg = ImageMaster.loadImage(Path.outlineRelicPath(imgName));

        }
    }

}
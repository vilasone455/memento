package vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ObtainCardLater extends AbstractGameEffect
{
    private AbstractCard card;

    public ObtainCardLater(AbstractCard card)
    {
        this.card = card;
        duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update()
    {
        
        isDone = true;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {

    }

    @Override
    public void dispose()
    {

    }
}
package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AmplifiedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(AmplifiedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private int baseCost;

    @Override
    public void onInitialApplication(AbstractCard card) {
        baseCost = Math.max(1, card.cost);
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return magic * (baseCost+1f)/baseCost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && cardCheck(card, c -> doesntUpgradeCost() && doesntDowngradeMagic() && c.baseMagicNumber > 0 && card.cost <= c.baseMagicNumber);
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AmplifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}

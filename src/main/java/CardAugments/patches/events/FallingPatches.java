package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.GraveMod;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.List;

public class FallingPatches {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("FallingEvent")).TEXT;
    public static int myIndex = -1;
    public static AbstractAugment augment;
    public static Object[] enumElements = null;
    @SpirePatch2(clz = Falling.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Falling __instance) {
            if (CardAugmentsMod.eventAddons) {
                augment = null;
                if (enumElements == null) {
                    try {
                        Class<?> enumElement = Class.forName(Falling.class.getName()+"$CurScreen");
                        if (enumElement.isEnum()) {
                            enumElements = enumElement.getEnumConstants();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = Falling.class, method = "buttonEffect")
    public static class AddButton {
        @SpirePostfixPatch()
        public static void add(Falling __instance, Object ___screen, boolean ___attack, boolean ___skill, boolean ___power) {
            if (CardAugmentsMod.eventAddons) {
                if (___screen == enumElements[1]) {
                    myIndex = __instance.imageEventText.optionList.size();
                    augment = new GraveMod();
                    if (___attack && ___power && ___skill) {
                        __instance.imageEventText.setDialogOption(MY_TEXT[0]);
                    } else {
                        __instance.imageEventText.setDialogOption(MY_TEXT[1], true);
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = Falling.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Falling __instance, @ByRef int[] buttonPressed, Object ___screen, AbstractCard ___skillCard, AbstractCard ___attackCard, AbstractCard ___powerCard) {
            if (CardAugmentsMod.eventAddons) {
                if (___screen == enumElements[1]) {
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(MY_TEXT[3]);
                        __instance.imageEventText.updateDialogOption(0, MY_TEXT[2]);
                        List<String> cardMetrics = new ArrayList<>();

                        if (___skillCard != null && augment.validCard(___skillCard)) {
                            CardModifierManager.addModifier(___skillCard, augment.makeCopy());
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(___skillCard.makeStatEquivalentCopy(), Settings.WIDTH * (1/4F), Settings.HEIGHT * (1/2F)));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH * (1/4F), Settings.HEIGHT * (1/2F)));
                            cardMetrics.add(___skillCard.cardID);
                        }
                        if (___powerCard != null && augment.validCard(___powerCard)) {
                            CardModifierManager.addModifier(___powerCard, augment.makeCopy());
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(___powerCard.makeStatEquivalentCopy(), Settings.WIDTH * (2/4F), Settings.HEIGHT * (1/2F)));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH * (2/4F), Settings.HEIGHT * (1/2F)));
                            cardMetrics.add(___powerCard.cardID);
                        }
                        if (___attackCard != null && augment.validCard(___attackCard)) {
                            CardModifierManager.addModifier(___attackCard, augment.makeCopy());
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(___attackCard.makeStatEquivalentCopy(), Settings.WIDTH * (3/4F), Settings.HEIGHT * (1/2F)));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH * (3/4F), Settings.HEIGHT * (1/2F)));
                            cardMetrics.add(___attackCard.cardID);
                        }
                        AbstractEvent.logMetric("Falling", "Exert", null, null, null, cardMetrics, null, null, null, 0, 0, 0, 0, 0, 0);

                        ReflectionHacks.setPrivate(__instance, Falling.class, "screen", enumElements[2]);
                        return SpireReturn.Return();
                    } else {
                        __instance.imageEventText.removeDialogOption(myIndex+1);
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}

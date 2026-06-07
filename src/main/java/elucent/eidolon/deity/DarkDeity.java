package elucent.eidolon.deity;

import elucent.eidolon.capability.Facts;
import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class DarkDeity extends Deity {
    public DarkDeity(ResourceLocation id, int red, int green, int blue) {
        super(id, red, green, blue);
    }

    @Override
    public void onReputationUnlock(EntityPlayer player, ReputationData reputation, ResourceLocation lock) {
        if (DeityLocks.SACRIFICE_MOB.equals(lock)) {
            KnowledgeUtil.grantSign(player, Signs.SOUL_SIGN);
        } else if (DeityLocks.SACRIFICE_VILLAGER.equals(lock)) {
            KnowledgeUtil.grantSign(player, Signs.MIND_SIGN);
        }
    }

    @Override
    public void onReputationChange(EntityPlayer player, ReputationData reputation, double previous, double current) {
        if (!KnowledgeUtil.knowsSign(player, Signs.BLOOD_SIGN)
                && (current >= 3.0D || reputation.hasLock(player, getId(), DeityLocks.SACRIFICE_MOB))) {
            reputation.setReputation(player, getId(), 3.0D);
            reputation.lock(player, getId(), DeityLocks.SACRIFICE_MOB);
            KnowledgeUtil.grantSign(player, Signs.BLOOD_SIGN);
        } else if (!KnowledgeUtil.knowsFact(player, Facts.VILLAGER_SACRIFICE)
                && (current >= 15.0D || reputation.hasLock(player, getId(), DeityLocks.SACRIFICE_VILLAGER))) {
            reputation.setReputation(player, getId(), 15.0D);
            reputation.lock(player, getId(), DeityLocks.SACRIFICE_VILLAGER);
            KnowledgeUtil.grantFact(player, Facts.VILLAGER_SACRIFICE);
        }
    }
}

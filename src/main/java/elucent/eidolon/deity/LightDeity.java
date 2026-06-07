package elucent.eidolon.deity;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LightDeity extends Deity {
    public LightDeity(ResourceLocation id, int red, int green, int blue) {
        super(id, red, green, blue);
    }

    @Override
    public void onReputationUnlock(EntityPlayer player, ReputationData reputation, ResourceLocation lock) {
        if (DeityLocks.BASIC_INCENSE_PRAYER.equals(lock)) {
            KnowledgeUtil.grantSign(player, Signs.SOUL_SIGN);
        }
    }

    @Override
    public void onReputationChange(EntityPlayer player, ReputationData reputation, double previous, double current) {
        if (!KnowledgeUtil.knowsSign(player, Signs.FLAME_SIGN) && current >= 3.0D) {
            reputation.setReputation(player, getId(), 3.0D);
            reputation.lock(player, getId(), DeityLocks.BASIC_INCENSE_PRAYER);
            KnowledgeUtil.grantSign(player, Signs.FLAME_SIGN);
        }
    }
}

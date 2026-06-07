package elucent.eidolon.item.curio;

import baubles.api.BaubleType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class PrestigiousPalmItem extends EidolonBaubleItem {
    private static final UUID REACH_ID = new UUID(297661999713141389L, 6434109711109552363L);
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_ID, "eidolon:prestigious_palm", 4.0D, 0);

    public PrestigiousPalmItem() {
        super(BaubleType.CHARM, "tooltip.eidolon.prestigious_palm");
    }

    public static void updateReach(EntityPlayer player, boolean equipped) {
        IAttributeInstance reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if (reach == null) {
            return;
        }
        AttributeModifier existing = reach.getModifier(REACH_ID);
        if (equipped && existing == null) {
            reach.applyModifier(REACH_MODIFIER);
        } else if (!equipped && existing != null) {
            reach.removeModifier(existing);
        }
    }
}

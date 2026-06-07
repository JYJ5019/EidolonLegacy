package elucent.eidolon.item.curio;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class GravityBeltItem extends EidolonBaubleItem {
    private static final double GRAVITY_COUNTERFORCE = 0.047D;

    public GravityBeltItem() {
        super(BaubleType.BELT, "tooltip.eidolon.gravity_belt");
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (entity.world.isRemote || entity.onGround) {
            return;
        }
        entity.motionY += GRAVITY_COUNTERFORCE;
        entity.velocityChanged = true;
    }
}

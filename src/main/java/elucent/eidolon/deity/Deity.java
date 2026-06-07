package elucent.eidolon.deity;

import elucent.eidolon.capability.ReputationData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class Deity {
    private final ResourceLocation id;
    private final int red;
    private final int green;
    private final int blue;

    protected Deity(ResourceLocation id, int red, int green, int blue) {
        this.id = id;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ResourceLocation getId() {
        return id;
    }

    public float getRed() {
        return red / 255.0F;
    }

    public float getGreen() {
        return green / 255.0F;
    }

    public float getBlue() {
        return blue / 255.0F;
    }

    public abstract void onReputationUnlock(EntityPlayer player, ReputationData reputation, ResourceLocation lock);

    public abstract void onReputationChange(EntityPlayer player, ReputationData reputation, double previous, double current);
}

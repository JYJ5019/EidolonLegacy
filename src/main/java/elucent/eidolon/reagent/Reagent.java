package elucent.eidolon.reagent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Reagent {
    private final ResourceLocation registryName;
    private final int color;
    private final boolean gas;

    public Reagent(ResourceLocation registryName, int color, boolean gas) {
        this.registryName = registryName;
        this.color = color;
        this.gas = gas;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getColor() {
        return color;
    }

    public boolean isGas() {
        return gas;
    }

    public abstract void worldEffect(World world, BlockPos pos, int amount);
}

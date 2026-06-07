package elucent.eidolon.spell;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Spell {
    private final ResourceLocation registryName;

    protected Spell(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public abstract boolean matches(SignSequence signs);

    public abstract boolean canCast(World world, BlockPos pos, EntityPlayer player, SignSequence signs);

    public abstract void cast(World world, BlockPos pos, EntityPlayer player, SignSequence signs);
}

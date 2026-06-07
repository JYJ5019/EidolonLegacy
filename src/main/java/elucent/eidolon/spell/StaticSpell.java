package elucent.eidolon.spell;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StaticSpell extends Spell {
    private final SignSequence signs;

    protected StaticSpell(ResourceLocation registryName, Sign... signs) {
        super(registryName);
        this.signs = new SignSequence(signs);
    }

    public SignSequence getSigns() {
        return new SignSequence(signs.getSigns());
    }

    @Override
    public boolean matches(SignSequence signs) {
        return this.signs.equals(signs);
    }

    public abstract boolean canCast(World world, BlockPos pos, EntityPlayer player);

    @Override
    public boolean canCast(World world, BlockPos pos, EntityPlayer player, SignSequence signs) {
        return canCast(world, pos, player);
    }

    public abstract void cast(World world, BlockPos pos, EntityPlayer player);

    @Override
    public void cast(World world, BlockPos pos, EntityPlayer player, SignSequence signs) {
        cast(world, pos, player);
    }
}

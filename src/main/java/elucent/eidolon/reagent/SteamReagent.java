package elucent.eidolon.reagent;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SteamReagent extends Reagent {
    public SteamReagent(ResourceLocation registryName) {
        super(registryName, 0xced9df, true);
    }

    @Override
    public void worldEffect(World world, BlockPos pos, int amount) {
        if (world == null || world.isRemote) {
            return;
        }
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.85F, 1.1F);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD,
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    24, 0.35D, 0.35D, 0.35D, 0.03D);
        }
    }
}

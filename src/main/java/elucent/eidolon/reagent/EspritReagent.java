package elucent.eidolon.reagent;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EspritReagent extends Reagent {
    public EspritReagent(ResourceLocation registryName) {
        super(registryName, 0x77b8e7, false);
    }

    @Override
    public void worldEffect(World world, BlockPos pos, int amount) {
        if (world == null || world.isRemote) {
            return;
        }
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.85F, 1.0F);
    }
}

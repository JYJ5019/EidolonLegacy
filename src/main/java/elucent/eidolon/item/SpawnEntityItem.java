package elucent.eidolon.item;

import elucent.eidolon.registries.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnEntityItem extends Item {
    private final String entityName;

    public SpawnEntityItem(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos spawnPos = pos.offset(facing);
        if (!world.isRemote) {
            Entity entity = ModEntities.create(entityName, world);
            if (entity == null) {
                return EnumActionResult.FAIL;
            }
            entity.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                    player.rotationYaw, 0.0F);
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
            }
            world.spawnEntity(entity);
            ItemStack stack = player.getHeldItem(hand);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}

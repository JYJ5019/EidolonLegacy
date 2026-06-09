package elucent.eidolon.spell;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DarkTouchSpell extends StaticSpell {
    public static final String NECROTIC_KEY = "eidolon:necrotic";

    public DarkTouchSpell(net.minecraft.util.ResourceLocation registryName, Sign... signs) {
        super(registryName, signs);
    }

    public static void init() {
        // Conversion rules are implemented in touchResult for 1.12 item/meta compatibility.
    }

    @Override
    public boolean canCast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return false;
        }
        if (ReputationData.get(world).getReputation(player, Deities.DARK_DEITY.getId()) < 4.0D) {
            return false;
        }
        EntityItem item = findTargetItem(world, player);
        return item != null && item.getItem().getCount() == 1 && !touchResult(item.getItem()).isEmpty();
    }

    @Override
    public void cast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        EntityItem item = findTargetItem(world, player);
        if (item == null || item.getItem().getCount() != 1) {
            return;
        }
        ItemStack result = touchResult(item.getItem());
        if (result.isEmpty()) {
            return;
        }
        item.setItem(result);
        item.setPickupDelay(20);
        SpellHelper.sendMagicBurst(world, item.posX, item.posY, item.posZ, Signs.WICKED_SIGN, Signs.BLOOD_SIGN);
    }

    private EntityItem findTargetItem(World world, EntityPlayer player) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d reach = eyes.add(look.scale(4.0D));
        RayTraceResult ray = world.rayTraceBlocks(eyes, reach, false, true, false);
        Vec3d target = ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK ? ray.hitVec : reach;
        return SpellHelper.singleNearbyItem(world, target.x, target.y, target.z, 1.5D);
    }

    private ItemStack touchResult(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ModItems.PEWTER_INLAY) {
            return new ItemStack(ModItems.UNHOLY_SYMBOL);
        }
        if (item == Item.getItemFromBlock(Blocks.WOOL) && stack.getMetadata() == 15) {
            return new ItemStack(ModItems.TOP_HAT);
        }
        if (item instanceof ItemRecord) {
            return new ItemStack(ModItems.MUSIC_DISC_PAROUSIA);
        }
        if (isSapling(stack)) {
            return new ItemStack(ModBlocks.ILLWOOD_SAPLING);
        }
        return ItemStack.EMPTY;
    }

    private boolean isSapling(ItemStack stack) {
        if (stack.getItem() == Item.getItemFromBlock(Blocks.SAPLING)) {
            return true;
        }
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            if ("treeSapling".equals(OreDictionary.getOreName(oreId))) {
                return true;
            }
        }
        return false;
    }
}

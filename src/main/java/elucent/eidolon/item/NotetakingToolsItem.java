package elucent.eidolon.item;

import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.registries.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NotetakingToolsItem extends Item {
    private static final String ROTATION_ROOT = "eidolonResearchNoteRotation";

    public NotetakingToolsItem() {
        setMaxStackSize(16);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        if (!isSelected || !world.isRemote || world.getTotalWorldTime() % 5L != 0L) {
            return;
        }
        BlockPos center = entity.getPosition();
        for (BlockPos pos : BlockPos.getAllInBox(center.add(-4, -4, -4), center.add(4, 4, 4))) {
            if (!Researches.getBlockResearches(world.getBlockState(pos).getBlock()).isEmpty()) {
                spawnResearchSparkles(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        1.0D, 0.5D, 10);
            }
        }
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(center).grow(4.0D, 4.0D, 4.0D),
                target -> target != null && target != entity && target.isEntityAlive());
        for (EntityLivingBase target : targets) {
            if (!Researches.getEntityResearches(target).isEmpty()) {
                spawnResearchSparkles(world, target.posX, target.posY + 0.3D, target.posZ,
                        Math.max(0.125D, target.width), 0.4D, 5);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, true);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = world.getBlockState(hit.getBlockPos()).getBlock();
            ResourceLocation id = block.getRegistryName();
            if (createNotes(player, stack, hand, Researches.getFluidResearches(block),
                    id == null ? "fluid:unknown" : "fluid:" + id)) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        if (createNotes(player, stack, hand, Researches.getDimensionResearches(player.dimension),
                "dimension:" + player.dimension)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();
        ResourceLocation id = block.getRegistryName();
        return createNotes(player, player.getHeldItem(hand), hand, Researches.getBlockResearches(block),
                id == null ? "block:unknown" : "block:" + id)
                ? EnumActionResult.SUCCESS
                : EnumActionResult.PASS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        ResourceLocation id = net.minecraft.entity.EntityList.getKey(target);
        return createNotes(player, stack, hand, Researches.getEntityResearches(target),
                id == null ? "entity:unknown" : "entity:" + id);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
        if (stack.isEmpty() || stack.getItem() != ModItems.NOTETAKING_TOOLS || !(event.getTarget() instanceof EntityLivingBase)) {
            return;
        }
        if (((NotetakingToolsItem) stack.getItem()).itemInteractionForEntity(
                stack, event.getEntityPlayer(), (EntityLivingBase) event.getTarget(), event.getHand())) {
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static boolean createNotes(EntityPlayer player, ItemStack tools, EnumHand hand,
                                       Collection<Research> researches, String sourceKey) {
        Research research = chooseResearch(player, researches, sourceKey);
        if (research == null) {
            return false;
        }
        if (!player.world.isRemote) {
            ItemStack notes = Researches.createNotes(research);
            tools.shrink(1);
            if (tools.isEmpty()) {
                player.setHeldItem(hand, notes);
            } else if (!player.inventory.addItemStackToInventory(notes)) {
                player.dropItem(notes, false);
            }
        }
        return true;
    }

    private static Research chooseResearch(EntityPlayer player, Collection<Research> researches, String sourceKey) {
        List<Research> unlocked = new ArrayList<>();
        for (Research research : researches) {
            if (research.isUnlockedFor(player)) {
                unlocked.add(research);
            }
        }
        if (unlocked.isEmpty()) {
            return null;
        }

        NBTTagCompound rotations = getRotationTag(player);
        int start = Math.floorMod(rotations.getInteger(sourceKey), unlocked.size());
        Research chosen = unlocked.get(start);
        rotations.setInteger(sourceKey, (start + 1) % unlocked.size());
        return chosen;
    }

    private static NBTTagCompound getRotationTag(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(ROTATION_ROOT)) {
            persisted.setTag(ROTATION_ROOT, new NBTTagCompound());
        }
        return persisted.getCompoundTag(ROTATION_ROOT);
    }

    private static void spawnResearchSparkles(World world, double x, double y, double z,
                                              double horizontalOffset, double verticalOffset, int count) {
        EidolonParticles.create(EidolonParticles.SPARKLE)
                .alpha(0.6F, 0.0F)
                .scale(0.15F, 0.0F)
                .lifetime(25)
                .randomOffset(horizontalOffset, verticalOffset)
                .addVelocity(0.0D, 0.1D, 0.0D)
                .color(0.33F, 0.38F, 0.91F)
                .spin(0.1F)
                .repeat(world, x, y, z, count);
    }
}

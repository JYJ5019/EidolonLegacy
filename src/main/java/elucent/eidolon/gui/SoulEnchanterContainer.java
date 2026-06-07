package elucent.eidolon.gui;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoulEnchanterContainer extends Container {
    private final SoulEnchanterTileEntity tile;
    private final InventoryBasic tableInventory = new InventoryBasic("soul_enchanter", false, 2);

    public SoulEnchanterContainer(InventoryPlayer playerInventory, SoulEnchanterTileEntity tile) {
        this.tile = tile;
        migrateLegacyTileInventory();

        addSlotToContainer(new Slot(tableInventory, SoulEnchanterTileEntity.SLOT_ITEM, 15, 47) {
            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return SoulEnchanterTileEntity.isEnchantableInput(stack);
            }
        });
        addSlotToContainer(new Slot(tableInventory, SoulEnchanterTileEntity.SLOT_SHARD, 35, 47) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.SOUL_SHARD;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private void migrateLegacyTileInventory() {
        if (tile.getWorld() != null && tile.getWorld().isRemote) {
            return;
        }
        for (int i = 0; i < tableInventory.getSizeInventory(); i++) {
            ItemStack stack = tile.removeStackFromSlot(i);
            if (!stack.isEmpty()) {
                tableInventory.setInventorySlotContents(i, stack);
            }
        }
    }

    public boolean canAttempt(EntityPlayer player, int tier) {
        ItemStack shards = tableInventory.getStackInSlot(SoulEnchanterTileEntity.SLOT_SHARD);
        EnchantmentOffer offer = getOffer(player, tier);
        return tier >= 1 && tier <= 3
                && offer != null
                && (player.capabilities.isCreativeMode || (shards.getCount() >= 1 && player.experienceLevel >= offer.level));
    }

    public int getOfferLevel(EntityPlayer player, int tier) {
        EnchantmentOffer offer = getOffer(player, tier);
        return offer == null ? 0 : offer.level;
    }

    public Enchantment getOfferEnchantment(EntityPlayer player, int tier) {
        EnchantmentOffer offer = getOffer(player, tier);
        return offer == null ? null : offer.enchantment;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        int tier = id + 1;
        if (!canAttempt(playerIn, tier)) {
            return false;
        }

        ItemStack input = tableInventory.getStackInSlot(SoulEnchanterTileEntity.SLOT_ITEM);
        EnchantmentOffer offer = getOffer(playerIn, tier);
        if (offer == null) {
            return false;
        }

        ItemStack enchanted = input;
        if (input.getItem() == Items.BOOK) {
            enchanted = new ItemStack(Items.ENCHANTED_BOOK);
            if (input.hasTagCompound()) {
                enchanted.setTagCompound(input.getTagCompound().copy());
            }
            tableInventory.setInventorySlotContents(SoulEnchanterTileEntity.SLOT_ITEM, enchanted);
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchanted);
        enchantments.put(offer.enchantment, offer.level);
        EnchantmentHelper.setEnchantments(enchantments, enchanted);

        if (!playerIn.capabilities.isCreativeMode) {
            tableInventory.decrStackSize(SoulEnchanterTileEntity.SLOT_SHARD, 1);
            playerIn.onEnchant(enchanted, offer.level);
        }
        tableInventory.markDirty();
        if (tile.getWorld() != null) {
            tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.BLOCKS, 0.8F, 0.85F + tile.getWorld().rand.nextFloat() * 0.3F);
        }
        return true;
    }

    private EnchantmentOffer getOffer(EntityPlayer player, int tier) {
        if (player == null || tier < 1 || tier > 3) {
            return null;
        }
        ItemStack stack = tableInventory.getStackInSlot(SoulEnchanterTileEntity.SLOT_ITEM);
        if (!SoulEnchanterTileEntity.isEnchantableInput(stack)) {
            return null;
        }
        List<Enchantment> candidates = getValidEnchantments(stack);
        if (candidates.isEmpty()) {
            return null;
        }
        int slot = tier - 1;
        Random random = new Random(player.getXPSeed() + slot);
        for (int i = 0; i < slot; i++) {
            random.nextInt(candidates.size());
        }
        Enchantment enchantment = candidates.get(random.nextInt(candidates.size()));
        Map<Enchantment, Integer> existing = EnchantmentHelper.getEnchantments(stack);
        int currentLevel = existing.containsKey(enchantment) ? existing.get(enchantment) : 0;
        int level = currentLevel > 0 ? currentLevel + 1 : 1;
        return new EnchantmentOffer(enchantment, level);
    }

    private List<Enchantment> getValidEnchantments(ItemStack stack) {
        ItemStack test = stack.copy();
        EnchantmentHelper.setEnchantments(new HashMap<>(), test);
        if (test.getItem() == Items.ENCHANTED_BOOK) {
            test = new ItemStack(Items.BOOK);
        }
        Map<Enchantment, Integer> existing = EnchantmentHelper.getEnchantments(stack);
        List<Enchantment> result = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment == null || enchantment.isCurse() || enchantment.isTreasureEnchantment()) {
                continue;
            }
            boolean canApply = test.getItem() == Items.BOOK ? enchantment.isAllowedOnBooks() : enchantment.canApply(test);
            if (!canApply || existing.getOrDefault(enchantment, 0) >= enchantment.getMaxLevel()) {
                continue;
            }
            boolean compatible = true;
            for (Enchantment present : existing.keySet()) {
                if (present != enchantment && !present.isCompatibleWith(enchantment)) {
                    compatible = false;
                    break;
                }
            }
            if (compatible) {
                result.add(enchantment);
            }
        }
        return result;
    }

    private static class EnchantmentOffer {
        private final Enchantment enchantment;
        private final int level;

        private EnchantmentOffer(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.SOUL_ENCHANTER
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        World world = tile.getWorld();
        if (world != null && !world.isRemote) {
            clearContainer(playerIn, world, tableInventory);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index < 2) {
                if (!mergeItemStack(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() == ModItems.SOUL_SHARD) {
                if (!mergeItemStack(stack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (SoulEnchanterTileEntity.isEnchantableInput(stack)) {
                if (!mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!mergeItemStack(stack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38 && !mergeItemStack(stack, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return copy;
    }
}

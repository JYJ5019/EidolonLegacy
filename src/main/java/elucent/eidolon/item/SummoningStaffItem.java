package elucent.eidolon.item;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

public class SummoningStaffItem extends Item {
    private static final String ABSORBED_UNDEAD = "AbsorbedUndead";
    private static final String ABSORBED_TYPES = "AbsorbedUndeadTypes";
    private static final String SELECTED_TYPE = "SelectedUndeadType";
    private static final String CHARGES = "charges";
    private static final String SELECTED_CHARGE = "selected";
    private static final int MAX_CHARGES = 100;
    private static final String LEGACY_TYPE = "minecraft:skeleton";
    public static final String SUMMONED_TAG = "EidolonSummoned";
    public static final String OWNER_TAG = "EidolonSummoner";

    public SummoningStaffItem() {
        setMaxStackSize(1);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public ItemStack addAbsorbedUndead(ItemStack stack, int amount) {
        return addAbsorbedUndead(stack, LEGACY_TYPE, amount);
    }

    public ItemStack addAbsorbedUndead(ItemStack stack, String entityId, int amount) {
        ItemStack charged = stack.copy();
        addAbsorbedUndeadInPlace(charged, entityId, amount);
        return charged;
    }

    public ItemStack addAbsorbedUndead(ItemStack stack, Map<String, Integer> absorbed) {
        ItemStack charged = stack.copy();
        for (Map.Entry<String, Integer> entry : absorbed.entrySet()) {
            addAbsorbedUndeadInPlace(charged, entry.getKey(), entry.getValue() * 5);
        }
        return charged;
    }

    public ItemStack addAbsorbedUndeadCharges(ItemStack stack, List<NBTTagCompound> charges) {
        ItemStack charged = stack.copy();
        addChargesInPlace(charged, charges);
        return charged;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                cycleSelectedType(stack);
            }
            playerIn.swingArm(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (getAbsorbedUndead(stack) <= 0) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        World world = entity.world;
        if (!world.isRemote || getAbsorbedUndead(stack) <= 0) {
            return;
        }
        RayTraceResult hit = rayTraceSummonTarget(entity);
        if (hit == null || hit.typeOfHit == RayTraceResult.Type.MISS) {
            return;
        }
        int used = getMaxItemUseDuration(stack) - count;
        float alpha = Math.max(0.35F, Math.min(1.0F, used / 30.0F));
        Vec3d pos = visualSummonTarget(hit);
        double angle = Math.toRadians((world.getTotalWorldTime() % 360L) + 12.0D * used);
        double radius = 0.42D + 0.5D * alpha;
        double dx = Math.sin(angle) * radius;
        double dz = Math.cos(angle) * radius;
        if (used == 40) {
            entity.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
        }
        if (used % 2 == 0) {
            spawnChargingRing(world, pos, angle, 0.72D + 0.34D * alpha, alpha, 16,
                    0.08D, pos.y + 0.42D, true);
        }
        if (used % 3 == 1) {
            spawnChargingRing(world, pos, -angle * 0.65D, 0.36D + 0.24D * alpha, alpha, 10,
                    0.22D, pos.y + 0.34D, false);
        }
        if (used % 5 == 0) {
            EidolonParticles.create(EidolonParticles.RING)
                    .randomVelocity(0.012D * alpha, 0.008D * alpha)
                    .color(0.42F, 0.64F, 0.24F, 0.7F, 0.34F, 1.0F)
                    .alpha(0.8F * alpha, 0.0F)
                    .scale(1.15F + 0.55F * alpha, 0.42F)
                    .lifetime(18)
                    .spin(0.05F + 0.04F * alpha)
                    .repeat(world, pos.x, pos.y + 0.3D, pos.z, 1);
        }
        EidolonParticles.create(EidolonParticles.SMOKE)
                .randomVelocity(0.04D * alpha, 0.02D * alpha)
                .addVelocity(0.0D, 0.016D, 0.0D)
                .color(38.0F / 255.0F, 34.0F / 255.0F, 26.0F / 255.0F,
                        16.0F / 255.0F, 10.0F / 255.0F, 22.0F / 255.0F)
                .alpha(0.5F * alpha, 0.0F)
                .randomOffset(0.16D + 0.12D * alpha, 0.1D + 0.1D * alpha)
                .scale(0.45F + 0.36F * alpha, alpha * 0.18F)
                .fullbright(false)
                .repeat(world, pos.x + dx, pos.y + 0.08D, pos.z + dz, 4)
                .repeat(world, pos.x - dx, pos.y + 0.08D, pos.z - dz, 4);
        EidolonParticles.create(EidolonParticles.WISP)
                .randomVelocity(0.04D * alpha, 0.03D * alpha)
                .addVelocity(0.0D, 0.02D, 0.0D)
                .color(0.54F, 0.78F, 0.3F, 0.72F, 0.32F, 0.98F)
                .alpha(1.0F * alpha, 0.0F)
                .scale(0.32F + 0.2F * alpha, 0.05F)
                .lifetime(22)
                .randomOffset(0.1D + 0.1D * alpha, 0.08D + 0.1D * alpha)
                .repeat(world, pos.x + dx, pos.y + 0.18D, pos.z + dz, 4)
                .repeat(world, pos.x - dx, pos.y + 0.18D, pos.z - dz, 4);
        EidolonParticles.create(EidolonParticles.WISP)
                .lineTarget(pos.x, pos.y + 0.45D, pos.z)
                .randomVelocity(0.04D, 0.04D)
                .color(0.68F, 0.9F, 0.36F, 0.76F, 0.32F, 1.0F)
                .alpha(1.0F * alpha, 0.0F)
                .scale(0.26F + 0.18F * alpha, 0.0F)
                .lifetime(14 + Math.min(14, used / 4))
                .randomOffset(0.05D, 0.05D)
                .spawn(world, pos.x + dx * 1.16D, pos.y + 0.08D, pos.z + dz * 1.16D)
                .spawn(world, pos.x - dx * 1.16D, pos.y + 0.08D, pos.z - dz * 1.16D);
        if (used % 3 == 0) {
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .randomVelocity(0.055D * alpha, 0.05D * alpha)
                    .addVelocity(0.0D, 0.06D, 0.0D)
                    .color(0.72F, 0.96F, 0.38F, 0.9F, 0.62F, 1.0F)
                    .alpha(1.0F, 0.0F)
                    .scale(0.15F + 0.08F * alpha, 0.0F)
                    .lifetime(24)
                    .randomOffset(0.28D + 0.14D * alpha, 0.12D)
                    .spin(0.24F)
                    .repeat(world, pos.x, pos.y + 0.34D, pos.z, 5);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (!(entityLiving instanceof EntityPlayer) || getMaxItemUseDuration(stack) - timeLeft < 20) {
            return;
        }
        RayTraceResult hit = rayTraceSummonTarget(entityLiving);
        if (hit == null || hit.typeOfHit == RayTraceResult.Type.MISS) {
            return;
        }
        if (worldIn.isRemote) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityLiving;
        Vec3d summonPos = visualSummonTarget(hit);
        if (hasCharges(stack)) {
            int selectedCharge = getSelectedChargeIndex(stack);
            Entity entity = createEntityFromCharge(stack, selectedCharge, worldIn);
            if (entity != null) {
                spawnSummonedEntity(worldIn, player, entity, false, summonPos);
            }
            if (!player.capabilities.isCreativeMode) {
                consumeCharge(stack, selectedCharge);
            }
            player.swingArm(player.getActiveHand());
            return;
        }
        String selected = getSelectedType(stack);
        int absorbed = getAbsorbedUndead(stack, selected);
        if (absorbed <= 0) {
            selected = findAvailableType(stack);
            absorbed = getAbsorbedUndead(stack, selected);
        }
        if (absorbed <= 0) {
            return;
        }
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(selected), worldIn);
        if (entity != null) {
            spawnSummonedEntity(worldIn, player, entity, true, summonPos);
            if (!player.capabilities.isCreativeMode) {
                setAbsorbedUndead(stack, selected, absorbed - 1);
            }
            player.swingArm(player.getActiveHand());
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String selected = hasCharges(stack) ? getSelectedChargeEntityId(stack) : getSelectedType(stack);
        tooltip.add(I18n.format("tooltip.eidolon.summoning_staff_selected", localizeEntity(selected)));
        tooltip.add(I18n.format("tooltip.eidolon.summoning_staff_absorbed", getAbsorbedUndead(stack)));
        for (String type : getAvailableTypes(stack)) {
            tooltip.add(I18n.format("tooltip.eidolon.summoning_staff_type", localizeEntity(type), getAbsorbedUndead(stack, type)));
        }
        tooltip.add(I18n.format("tooltip.eidolon.summoning_staff_cycle"));
        tooltip.add(I18n.format("tooltip.eidolon.summoning_staff_use"));
    }

    private int getAbsorbedUndead(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound types = tag.getCompoundTag(ABSORBED_TYPES);
        int total = getChargeCount(stack) + tag.getInteger(ABSORBED_UNDEAD);
        for (String key : types.getKeySet()) {
            total += types.getInteger(key);
        }
        return total;
    }

    private int getAbsorbedUndead(ItemStack stack, String entityId) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        int count = getChargeCount(stack, entityId) + tag.getCompoundTag(ABSORBED_TYPES).getInteger(entityId);
        if (LEGACY_TYPE.equals(entityId)) {
            count += tag.getInteger(ABSORBED_UNDEAD);
        }
        return count;
    }

    private void setAbsorbedUndead(ItemStack stack, String entityId, int amount) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if (LEGACY_TYPE.equals(entityId)) {
            tag.setInteger(ABSORBED_UNDEAD, 0);
        }
        NBTTagCompound types = tag.getCompoundTag(ABSORBED_TYPES);
        types.setInteger(entityId, Math.max(0, amount));
        tag.setTag(ABSORBED_TYPES, types);
    }

    private void addAbsorbedUndeadInPlace(ItemStack stack, String entityId, int amount) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound types = tag.getCompoundTag(ABSORBED_TYPES);
        types.setInteger(entityId, Math.max(0, types.getInteger(entityId) + amount));
        tag.setTag(ABSORBED_TYPES, types);
        tag.setString(SELECTED_TYPE, entityId);
    }

    private String getSelectedType(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return LEGACY_TYPE;
        }
        NBTTagCompound tag = stack.getTagCompound();
        String selected = tag.getString(SELECTED_TYPE);
        return selected.isEmpty() ? LEGACY_TYPE : selected;
    }

    private String findAvailableType(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return LEGACY_TYPE;
        }
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound types = tag.getCompoundTag(ABSORBED_TYPES);
        for (String key : types.getKeySet()) {
            if (types.getInteger(key) > 0) {
                tag.setString(SELECTED_TYPE, key);
                return key;
            }
        }
        if (tag.getInteger(ABSORBED_UNDEAD) > 0) {
            tag.setString(SELECTED_TYPE, LEGACY_TYPE);
            return LEGACY_TYPE;
        }
        return getSelectedType(stack);
    }

    private void cycleSelectedType(ItemStack stack) {
        if (hasCharges(stack)) {
            NBTTagCompound tag = getOrCreateTag(stack);
            int next = (getSelectedChargeIndex(stack) + 1) % getChargeCount(stack);
            tag.setInteger(SELECTED_CHARGE, next);
            tag.setString(SELECTED_TYPE, getSelectedChargeEntityId(stack));
            return;
        }
        List<String> types = getAvailableTypes(stack);
        if (types.isEmpty()) {
            return;
        }
        String selected = getSelectedType(stack);
        int index = types.indexOf(selected);
        String next = types.get((index + 1) % types.size());
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setString(SELECTED_TYPE, next);
    }

    private List<String> getAvailableTypes(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return Collections.emptyList();
        }
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound typesTag = tag.getCompoundTag(ABSORBED_TYPES);
        List<String> types = new ArrayList<>();
        NBTTagList charges = getCharges(stack);
        for (int i = 0; i < charges.tagCount(); i++) {
            String id = getChargeEntityId(charges.getCompoundTagAt(i));
            if (!id.isEmpty() && !types.contains(id)) {
                types.add(id);
            }
        }
        for (String key : typesTag.getKeySet()) {
            if (typesTag.getInteger(key) > 0) {
                if (!types.contains(key)) {
                    types.add(key);
                }
            }
        }
        if (tag.getInteger(ABSORBED_UNDEAD) > 0 && !types.contains(LEGACY_TYPE)) {
            types.add(LEGACY_TYPE);
        }
        Collections.sort(types);
        return types;
    }

    private void addChargesInPlace(ItemStack stack, List<NBTTagCompound> charges) {
        if (charges.isEmpty()) {
            return;
        }
        NBTTagCompound tag = getOrCreateTag(stack);
        NBTTagList list = getCharges(stack);
        boolean wasEmpty = list.tagCount() == 0;
        for (NBTTagCompound charge : charges) {
            if (charge != null && charge.hasKey("id")) {
                list.appendTag(charge.copy());
            }
        }
        while (list.tagCount() > MAX_CHARGES) {
            list.removeTag(list.tagCount() - 1);
        }
        tag.setTag(CHARGES, list);
        if (wasEmpty && list.tagCount() > 0) {
            tag.setInteger(SELECTED_CHARGE, 0);
            tag.setString(SELECTED_TYPE, getChargeEntityId(list.getCompoundTagAt(0)));
        }
    }

    private boolean hasCharges(ItemStack stack) {
        return getChargeCount(stack) > 0;
    }

    private int getChargeCount(ItemStack stack) {
        return getCharges(stack).tagCount();
    }

    private int getChargeCount(ItemStack stack, String entityId) {
        NBTTagList charges = getCharges(stack);
        int count = 0;
        for (int i = 0; i < charges.tagCount(); i++) {
            if (entityId.equals(getChargeEntityId(charges.getCompoundTagAt(i)))) {
                count++;
            }
        }
        return count;
    }

    private NBTTagList getCharges(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return new NBTTagList();
        }
        return stack.getTagCompound().getTagList(CHARGES, Constants.NBT.TAG_COMPOUND);
    }

    private int getSelectedChargeIndex(ItemStack stack) {
        int count = getChargeCount(stack);
        if (count <= 0) {
            return 0;
        }
        NBTTagCompound tag = getOrCreateTag(stack);
        int selected = tag.getInteger(SELECTED_CHARGE);
        if (selected < 0 || selected >= count) {
            selected = 0;
            tag.setInteger(SELECTED_CHARGE, selected);
        }
        return selected;
    }

    private String getSelectedChargeEntityId(ItemStack stack) {
        NBTTagList charges = getCharges(stack);
        if (charges.tagCount() <= 0) {
            return getSelectedType(stack);
        }
        return getChargeEntityId(charges.getCompoundTagAt(getSelectedChargeIndex(stack)));
    }

    private String getChargeEntityId(NBTTagCompound charge) {
        return charge == null ? "" : charge.getString("id");
    }

    private Entity createEntityFromCharge(ItemStack stack, int index, World world) {
        NBTTagList charges = getCharges(stack);
        if (index < 0 || index >= charges.tagCount()) {
            return null;
        }
        NBTTagCompound entityTag = charges.getCompoundTagAt(index).copy();
        entityTag.removeTag("UUID");
        entityTag.removeTag("UUIDMost");
        entityTag.removeTag("UUIDLeast");
        return EntityList.createEntityFromNBT(entityTag, world);
    }

    private void consumeCharge(ItemStack stack, int index) {
        NBTTagCompound tag = getOrCreateTag(stack);
        NBTTagList charges = getCharges(stack);
        if (index >= 0 && index < charges.tagCount()) {
            charges.removeTag(index);
        }
        tag.setTag(CHARGES, charges);
        if (charges.tagCount() <= 0) {
            tag.setInteger(SELECTED_CHARGE, 0);
            return;
        }
        int selected = Math.min(index, charges.tagCount() - 1);
        tag.setInteger(SELECTED_CHARGE, selected);
        tag.setString(SELECTED_TYPE, getChargeEntityId(charges.getCompoundTagAt(selected)));
    }

    private void spawnSummonedEntity(World world, EntityPlayer player, Entity entity, boolean initialize, Vec3d position) {
        entity.setPosition(position.x, position.y, position.z);
        entity.getEntityData().setBoolean(SUMMONED_TAG, true);
        entity.getEntityData().setString(OWNER_TAG, player.getUniqueID().toString());
        if (initialize && entity instanceof EntityLiving) {
            ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
        }
        world.spawnEntity(entity);
        double centerY = entity.posY + entity.height * 0.5D;
        VisualEffectPacket.sendAround(world, entity.posX, centerY, entity.posZ,
                VisualEffectPacket.at(VisualEffectPacket.SUMMON_BURST, entity.posX,
                        centerY, entity.posZ,
                        61.0F / 255.0F, 70.0F / 255.0F, 35.0F / 255.0F,
                        36.0F / 255.0F, 24.0F / 255.0F, 41.0F / 255.0F));
        VisualEffectPacket.sendAround(world, entity.posX, entity.posY + 0.12D, entity.posZ,
                VisualEffectPacket.at(VisualEffectPacket.SUMMON_BURST, entity.posX,
                        entity.posY + 0.12D, entity.posZ,
                        61.0F / 255.0F, 70.0F / 255.0F, 35.0F / 255.0F,
                        36.0F / 255.0F, 24.0F / 255.0F, 41.0F / 255.0F));
        world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS, 0.75F, 0.1F);
    }

    private void spawnChargingRing(World world, Vec3d pos, double angleOffset, double radius, float alpha, int amount,
                                   double yOffset, double targetY, boolean brightCore) {
        for (int i = 0; i < amount; i++) {
            double angle = angleOffset + Math.PI * 2.0D * i / amount;
            double x = pos.x + Math.sin(angle) * radius;
            double z = pos.z + Math.cos(angle) * radius;
            double y = pos.y + yOffset + (i % 3) * 0.045D;
            EidolonParticles.create(EidolonParticles.WISP)
                    .lineTarget(pos.x, targetY, pos.z)
                    .randomVelocity(0.018D * alpha, 0.024D * alpha)
                    .color(brightCore ? 0.62F : 0.52F, brightCore ? 0.86F : 0.64F,
                            brightCore ? 0.34F : 0.86F, 0.78F, 0.34F, 1.0F)
                    .alpha((brightCore ? 1.0F : 0.84F) * alpha, 0.0F)
                    .scale((brightCore ? 0.24F : 0.18F) + 0.12F * alpha, 0.0F)
                    .lifetime(16 + (int) (10.0F * alpha))
                    .randomOffset(0.035D, 0.04D)
                    .spawn(world, x, y, z);
            if (brightCore && i % 4 == 0) {
                EidolonParticles.create(EidolonParticles.SPARKLE)
                        .randomVelocity(0.03D * alpha, 0.04D * alpha)
                        .addVelocity(0.0D, 0.035D, 0.0D)
                        .color(0.8F, 1.0F, 0.46F, 0.9F, 0.64F, 1.0F)
                        .alpha(1.0F, 0.0F)
                        .scale(0.12F + 0.05F * alpha, 0.0F)
                        .lifetime(20)
                        .spin(0.22F)
                        .spawn(world, x, y + 0.04D, z);
            }
        }
    }

    private Vec3d visualSummonTarget(RayTraceResult hit) {
        Vec3d pos = hit.hitVec;
        if (hit.sideHit != null) {
            Vec3i direction = hit.sideHit.getDirectionVec();
            pos = new Vec3d(pos.x + direction.getX() * 0.08D,
                    pos.y + direction.getY() * 0.08D,
                    pos.z + direction.getZ() * 0.08D);
        }
        return new Vec3d(pos.x, pos.y + 0.12D, pos.z);
    }

    private RayTraceResult rayTraceSummonTarget(EntityLivingBase entity) {
        Vec3d eyes = entity.getPositionEyes(1.0F);
        Vec3d look = entity.getLook(1.0F);
        Vec3d target = new Vec3d(eyes.x + look.x * 16.0D, eyes.y + look.y * 16.0D, eyes.z + look.z * 16.0D);
        return entity.world.rayTraceBlocks(eyes, target, false, true, false);
    }

    private NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
    }

    private String localizeEntity(String entityId) {
        ResourceLocation id = new ResourceLocation(entityId);
        return I18n.format("entity." + id.getNamespace() + "." + id.getPath() + ".name");
    }
}

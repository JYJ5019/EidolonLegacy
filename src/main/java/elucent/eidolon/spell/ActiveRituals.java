package elucent.eidolon.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.entity.ai.GoToPositionGoal;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.tile.BrazierTileEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActiveRituals extends WorldSavedData {
    private static final String DATA_NAME = Reference.MOD_ID + "_active_rituals";
    private static final AxisAlignedBB DEFAULT_CRYSTAL_BOUNDS =
            new AxisAlignedBB(-8.0D, -6.0D, -8.0D, 9.0D, 11.0D, 9.0D);

    private final List<Entry> entries = new ArrayList<>();

    public ActiveRituals() {
        super(DATA_NAME);
    }

    public ActiveRituals(String name) {
        super(name);
    }

    public static void activate(World world, BlockPos origin, AltarRitual ritual) {
        if (world == null || world.isRemote || origin == null || ritual == null || !ritual.isFieldRitual()) {
            return;
        }
        ActiveRituals data = get(world);
        for (Entry entry : data.entries) {
            if (entry.pos.equals(origin) && entry.ritualId.equals(ritual.getId())) {
                entry.ticks = 0;
                data.markDirty();
                return;
            }
        }
        data.entries.add(new Entry(ritual.getId(), origin));
        data.markDirty();
    }

    public static void tick(World world) {
        if (world == null || world.isRemote) {
            return;
        }
        get(world).tickEntries(world);
    }

    public static void performCrystal(World world, BlockPos origin) {
        if (world == null || world.isRemote || origin == null) {
            return;
        }
        AxisAlignedBB bounds = DEFAULT_CRYSTAL_BOUNDS.offset(origin);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds,
                entity -> entity != null
                        && entity.isEntityAlive()
                        && entity.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD);
        for (EntityLivingBase entity : entities) {
            VisualEffectPacket.sendAround(world, entity.posX, entity.posY + entity.height * 0.5D, entity.posZ,
                    VisualEffectPacket.at(VisualEffectPacket.CRYSTALLIZE, entity.posX,
                            entity.posY + entity.height * 0.5D, entity.posZ, 0.97F, 0.61F, 0.86F));
            entity.attackEntityFrom(Eidolon.RITUAL_DAMAGE, entity.getMaxHealth() * 1000.0F);
            if (entity.isEntityAlive()) {
                entity.setDead();
            }
            EntityItem shard = new EntityItem(world, entity.posX, entity.posY, entity.posZ,
                    new ItemStack(ModItems.SOUL_SHARD, 1 + world.rand.nextInt(3)));
            world.spawnEntity(shard);
        }
    }

    private static ActiveRituals get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        if (storage == null) {
            storage = world.getMapStorage();
        }
        ActiveRituals data = storage == null ? null
                : (ActiveRituals) storage.getOrLoadData(ActiveRituals.class, DATA_NAME);
        if (data == null) {
            data = new ActiveRituals();
            if (storage != null) {
                storage.setData(DATA_NAME, data);
            }
        }
        return data;
    }

    private void tickEntries(World world) {
        if (entries.isEmpty()) {
            return;
        }
        boolean changed = false;
        Iterator<Entry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            AltarRitual ritual = AltarRituals.find(entry.ritualId);
            if (ritual == null || !ritual.isFieldRitual()) {
                iterator.remove();
                changed = true;
                continue;
            }
            TickResult result = tickEntry(world, entry, ritual);
            if (result == TickResult.REMOVE) {
                iterator.remove();
                changed = true;
            } else if (result == TickResult.DIRTY) {
                changed = true;
            }
        }
        if (changed) {
            markDirty();
        }
    }

    private TickResult tickEntry(World world, Entry entry, AltarRitual ritual) {
        if (!isAnchorValid(world, entry.pos)) {
            return world.isBlockLoaded(entry.pos) ? TickResult.REMOVE : TickResult.KEEP;
        }
        entry.ticks++;
        if (ritual.getBehaviorType() == AltarRitual.BehaviorType.DAYLIGHT) {
            return tickDaylight(world, entry.pos);
        }
        if (ritual.getBehaviorType() == AltarRitual.BehaviorType.MOONLIGHT) {
            return tickMoonlight(world, entry.pos);
        }
        if (ritual.getBehaviorType() == AltarRitual.BehaviorType.ALLURE) {
            tickAllure(world, entry.pos);
            return entry.ticks % 200 == 0 ? TickResult.DIRTY : TickResult.KEEP;
        }
        if (ritual.getBehaviorType() == AltarRitual.BehaviorType.REPELLING) {
            tickRepelling(world, entry.pos);
            return entry.ticks % 200 == 0 ? TickResult.DIRTY : TickResult.KEEP;
        }
        if (ritual.getBehaviorType() == AltarRitual.BehaviorType.DECEIT) {
            tickDeceit(world, entry.pos);
            return entry.ticks % 20 == 0 ? TickResult.DIRTY : TickResult.KEEP;
        }
        return TickResult.REMOVE;
    }

    private boolean isAnchorValid(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        if (world.getBlockState(pos).getBlock() == ModBlocks.STONE_ALTAR
                || world.getBlockState(pos).getBlock() == ModBlocks.WOODEN_ALTAR
                || world.getBlockState(pos).getBlock() == ModBlocks.BRAZIER) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof BrazierTileEntity;
    }

    private TickResult tickDaylight(World world, BlockPos pos) {
        long time = positiveDayTime(world);
        if (time < 1000L || time >= 12000L) {
            advanceTimeAndSync(world);
            pulse(world, pos, 1.0F, 0.96F, 0.51F, EnumParticleTypes.SPELL);
            return TickResult.DIRTY;
        }
        pulseComplete(world, pos, 1.0F, 0.96F, 0.51F);
        return TickResult.REMOVE;
    }

    private TickResult tickMoonlight(World world, BlockPos pos) {
        long time = positiveDayTime(world);
        if (time < 13000L) {
            advanceTimeAndSync(world);
            pulse(world, pos, 0.44F, 0.29F, 0.74F, EnumParticleTypes.SPELL_WITCH);
            return TickResult.DIRTY;
        }
        pulseComplete(world, pos, 0.44F, 0.29F, 0.74F);
        return TickResult.REMOVE;
    }

    private void tickAllure(World world, BlockPos pos) {
        if (world.getTotalWorldTime() % 200L != 0L) {
            ambientPulse(world, pos, 1.0F, 0.17F, 0.29F);
            return;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(96.0D, 16.0D, 96.0D);
        List<EntityAnimal> animals = world.getEntitiesWithinAABB(EntityAnimal.class, bounds,
                animal -> animal != null && animal.isEntityAlive());
        for (EntityAnimal animal : animals) {
            boolean hasGoal = hasGoToPositionGoal(animal);
            double distance = animal.getDistanceSq(pos);
            if (!hasGoal && distance >= 12.0D * 12.0D && world.rand.nextInt(40) == 0) {
                BlockPos target = pos.down().add(world.rand.nextInt(9) - 4, 0, world.rand.nextInt(9) - 4);
                animal.tasks.addTask(1, new GoToPositionGoal(animal, target, 1.0D));
            } else if (hasGoal && distance < 8.0D * 8.0D) {
                removeGoToPositionGoals(animal);
            }
        }
        pulse(world, pos, 1.0F, 0.17F, 0.29F, EnumParticleTypes.HEART);
    }

    private void tickRepelling(World world, BlockPos pos) {
        if (world.getTotalWorldTime() % 200L != 0L) {
            ambientPulse(world, pos, 0.75F, 0.83F, 0.72F);
            return;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(96.0D, 16.0D, 96.0D);
        List<EntityMob> monsters = world.getEntitiesWithinAABB(EntityMob.class, bounds,
                monster -> monster != null && monster.isEntityAlive());
        double centerX = pos.getX() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;
        for (EntityMob monster : monsters) {
            boolean hasGoal = hasGoToPositionGoal(monster);
            double distance = monster.getDistanceSq(pos);
            double dx = monster.posX - centerX;
            double dz = monster.posZ - centerZ;
            double length = Math.sqrt(dx * dx + dz * dz);
            if (!hasGoal && distance <= 80.0D * 80.0D) {
                if (length < 0.001D) {
                    dx = world.rand.nextBoolean() ? 1.0D : -1.0D;
                    dz = world.rand.nextBoolean() ? 1.0D : -1.0D;
                    length = Math.sqrt(dx * dx + dz * dz);
                }
                double targetX = centerX + dx / length * 90.0D;
                double targetZ = centerZ + dz / length * 90.0D;
                BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(targetX, 0.0D, targetZ));
                monster.tasks.addTask(1, new GoToPositionGoal(monster, target, 1.0D));
            } else if (hasGoal && distance > 88.0D * 88.0D) {
                removeGoToPositionGoals(monster);
            }
        }
        pulse(world, pos, 0.75F, 0.83F, 0.72F, EnumParticleTypes.SPELL);
    }

    private boolean hasGoToPositionGoal(EntityCreature creature) {
        boolean hasGoal = false;
        List<GoToPositionGoal> staleGoals = new ArrayList<>();
        for (EntityAITasks.EntityAITaskEntry entry : creature.tasks.taskEntries) {
            if (entry.action instanceof GoToPositionGoal) {
                GoToPositionGoal goal = (GoToPositionGoal) entry.action;
                if (goal.isRunning()) {
                    hasGoal = true;
                } else {
                    staleGoals.add(goal);
                }
            }
        }
        for (GoToPositionGoal goal : staleGoals) {
            creature.tasks.removeTask(goal);
        }
        return hasGoal;
    }

    private void removeGoToPositionGoals(EntityCreature creature) {
        List<GoToPositionGoal> goals = new ArrayList<>();
        for (EntityAITasks.EntityAITaskEntry entry : creature.tasks.taskEntries) {
            if (entry.action instanceof GoToPositionGoal) {
                goals.add((GoToPositionGoal) entry.action);
            }
        }
        for (GoToPositionGoal goal : goals) {
            creature.tasks.removeTask(goal);
        }
    }

    private void tickDeceit(World world, BlockPos pos) {
        if (world.getTotalWorldTime() % 20L != 0L) {
            ambientPulse(world, pos, 0.25F, 1.0F, 0.38F);
            return;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(48.0D, 16.0D, 48.0D);
        List<EntityVillager> villagers = world.getEntitiesWithinAABB(EntityVillager.class, bounds,
                villager -> villager != null && villager.isEntityAlive());
        for (EntityVillager villager : villagers) {
            if (world.rand.nextInt(120) == 0) {
                decayVillageReputation(world, villager, bounds);
                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
                            villager.posX, villager.posY + villager.height + 0.2D, villager.posZ,
                            4, 0.18D, 0.12D, 0.18D, 0.01D);
                }
            }
        }
        pulse(world, pos, 0.25F, 1.0F, 0.38F, EnumParticleTypes.VILLAGER_HAPPY);
    }

    private void decayVillageReputation(World world, EntityVillager villager, AxisAlignedBB bounds) {
        Village village = world.villageCollection.getNearestVillage(villager.getPosition(), 32);
        if (village == null) {
            return;
        }
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds,
                player -> player != null && player.isEntityAlive());
        for (EntityPlayer player : players) {
            int reputation = village.getPlayerReputation(player.getName());
            if (reputation < 0) {
                village.modifyPlayerReputation(player.getName(), 1);
            } else if (reputation > 0) {
                village.modifyPlayerReputation(player.getName(), -1);
            }
        }
    }

    private long positiveDayTime(World world) {
        long time = world.getWorldTime() % 24000L;
        return time < 0L ? time + 24000L : time;
    }

    private void advanceTimeAndSync(World world) {
        world.setWorldTime(world.getWorldTime() + 100L);
        if (!(world instanceof WorldServer)) {
            return;
        }
        SPacketTimeUpdate packet = new SPacketTimeUpdate(world.getTotalWorldTime(), world.getWorldTime(),
                world.getGameRules().getBoolean("doDaylightCycle"));
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.sendPacket(packet);
            }
        }
    }

    private void ambientPulse(World world, BlockPos pos, float r, float g, float b) {
        if (world.getTotalWorldTime() % 80L == 0L) {
            pulse(world, pos, r, g, b, EnumParticleTypes.SPELL_MOB_AMBIENT);
        }
    }

    private void pulse(World world, BlockPos pos, float r, float g, float b, EnumParticleTypes particle) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(particle,
                    pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                    8, 0.35D, 0.22D, 0.35D, 0.015D);
        }
        VisualEffectPacket.sendAround(world, pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                VisualEffectPacket.at(VisualEffectPacket.MAGIC_BURST,
                        pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D, r, g, b));
    }

    private void pulseComplete(World world, BlockPos pos, float r, float g, float b) {
        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.45F, 1.45F);
        VisualEffectPacket.sendAround(world, pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                VisualEffectPacket.at(VisualEffectPacket.RITUAL_COMPLETE,
                        pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D, r, g, b));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        entries.clear();
        NBTTagList list = nbt.getTagList("entries", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            Entry entry = Entry.read(list.getCompoundTagAt(i));
            if (entry != null) {
                entries.add(entry);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Entry entry : entries) {
            list.appendTag(entry.write());
        }
        compound.setTag("entries", list);
        return compound;
    }

    private enum TickResult {
        KEEP,
        DIRTY,
        REMOVE
    }

    private static final class Entry {
        private final ResourceLocation ritualId;
        private final BlockPos pos;
        private int ticks;

        private Entry(ResourceLocation ritualId, BlockPos pos) {
            this.ritualId = ritualId;
            this.pos = pos.toImmutable();
        }

        private NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ritual", ritualId.toString());
            tag.setInteger("x", pos.getX());
            tag.setInteger("y", pos.getY());
            tag.setInteger("z", pos.getZ());
            tag.setInteger("ticks", ticks);
            return tag;
        }

        private static Entry read(NBTTagCompound tag) {
            if (!tag.hasKey("ritual", Constants.NBT.TAG_STRING)) {
                return null;
            }
            Entry entry = new Entry(new ResourceLocation(tag.getString("ritual")),
                    new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
            entry.ticks = tag.getInteger("ticks");
            return entry;
        }
    }
}

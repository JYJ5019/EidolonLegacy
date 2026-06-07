package elucent.eidolon.capability;

import elucent.eidolon.Reference;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.deity.Deity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReputationData extends WorldSavedData {
    private static final String DATA_NAME = Reference.MOD_ID + "_reputation";

    private final Map<UUID, Map<ResourceLocation, ReputationEntry>> reputationMap = new HashMap<>();
    private final Map<UUID, Map<ResourceLocation, Long>> prayerTimes = new HashMap<>();

    public ReputationData() {
        super(DATA_NAME);
    }

    public ReputationData(String name) {
        super(name);
    }

    public static ReputationData get(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage == null) {
            storage = world.getPerWorldStorage();
        }
        ReputationData data = (ReputationData) storage.getOrLoadData(ReputationData.class, DATA_NAME);
        if (data == null) {
            data = new ReputationData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public double getReputation(EntityPlayer player, ResourceLocation deity) {
        return getEntry(player.getUniqueID(), deity).getReputation();
    }

    public void addReputation(EntityPlayer player, ResourceLocation deity, double amount) {
        double prev = getReputation(player, deity);
        ReputationEntry entry = getEntry(player.getUniqueID(), deity);
        if (entry.getLock() == null) {
            entry.setReputation(entry.getReputation() + amount);
            markDirty();
            considerChange(player, deity, prev);
        }
    }

    public void subtractReputation(EntityPlayer player, ResourceLocation deity, double amount) {
        double prev = getReputation(player, deity);
        ReputationEntry entry = getEntry(player.getUniqueID(), deity);
        entry.setReputation(Math.max(0.0D, entry.getReputation() - amount));
        markDirty();
        considerChange(player, deity, prev);
    }

    public void setReputation(EntityPlayer player, ResourceLocation deity, double amount) {
        double prev = getReputation(player, deity);
        ReputationEntry entry = getEntry(player.getUniqueID(), deity);
        if (entry.getLock() == null || amount < 0.0D) {
            entry.setReputation(amount);
            markDirty();
            considerChange(player, deity, prev);
        }
    }

    public boolean isLocked(EntityPlayer player, ResourceLocation deity) {
        return getEntry(player.getUniqueID(), deity).getLock() != null;
    }

    public boolean hasLock(EntityPlayer player, ResourceLocation deity, ResourceLocation lock) {
        ResourceLocation current = getEntry(player.getUniqueID(), deity).getLock();
        return current != null && current.equals(lock);
    }

    public void lock(EntityPlayer player, ResourceLocation deity, ResourceLocation lock) {
        ReputationEntry entry = getEntry(player.getUniqueID(), deity);
        entry.setLock(lock);
        markDirty();
    }

    public boolean unlock(EntityPlayer player, ResourceLocation deity, ResourceLocation lock) {
        ReputationEntry entry = getEntry(player.getUniqueID(), deity);
        if (entry.getLock() != null && entry.getLock().equals(lock)) {
            entry.setLock(null);
            markDirty();
            Deity found = Deities.find(deity);
            if (found != null) {
                found.onReputationUnlock(player, this, lock);
            }
            return true;
        }
        return false;
    }

    public void pray(EntityPlayer player, ResourceLocation spell, long time) {
        prayerTimes.computeIfAbsent(player.getUniqueID(), key -> new HashMap<>()).put(spell, time);
        markDirty();
    }

    public boolean canPray(EntityPlayer player, ResourceLocation spell, long time) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        Long last = prayerTimes.computeIfAbsent(player.getUniqueID(), key -> new HashMap<>()).get(spell);
        return last == null || last < time - 21000L;
    }

    private ReputationEntry getEntry(UUID player, ResourceLocation deity) {
        return reputationMap
                .computeIfAbsent(player, key -> new HashMap<>())
                .computeIfAbsent(deity, key -> new ReputationEntry());
    }

    private void considerChange(EntityPlayer player, ResourceLocation deity, double prev) {
        double current = getReputation(player, deity);
        if (current == prev) {
            return;
        }
        Deity found = Deities.find(deity);
        if (found != null) {
            found.onReputationChange(player, this, prev, current);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        reputationMap.clear();
        prayerTimes.clear();
        NBTTagCompound reps = nbt.getCompoundTag("reps");
        for (String uuidString : reps.getKeySet()) {
            UUID uuid = UUID.fromString(uuidString);
            NBTTagCompound playerTag = reps.getCompoundTag(uuidString);
            Map<ResourceLocation, ReputationEntry> entries = reputationMap.computeIfAbsent(uuid, key -> new HashMap<>());
            for (String deity : playerTag.getKeySet()) {
                ReputationEntry entry = new ReputationEntry();
                entry.readFromNBT(playerTag.getCompoundTag(deity));
                entries.put(new ResourceLocation(deity), entry);
            }
        }
        NBTTagCompound times = nbt.getCompoundTag("times");
        for (String uuidString : times.getKeySet()) {
            UUID uuid = UUID.fromString(uuidString);
            NBTTagCompound playerTag = times.getCompoundTag(uuidString);
            Map<ResourceLocation, Long> entries = prayerTimes.computeIfAbsent(uuid, key -> new HashMap<>());
            for (String spell : playerTag.getKeySet()) {
                entries.put(new ResourceLocation(spell), playerTag.getLong(spell));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound reps = new NBTTagCompound();
        for (Map.Entry<UUID, Map<ResourceLocation, ReputationEntry>> playerEntry : reputationMap.entrySet()) {
            NBTTagCompound playerTag = new NBTTagCompound();
            for (Map.Entry<ResourceLocation, ReputationEntry> deityEntry : playerEntry.getValue().entrySet()) {
                playerTag.setTag(deityEntry.getKey().toString(), deityEntry.getValue().writeToNBT());
            }
            reps.setTag(playerEntry.getKey().toString(), playerTag);
        }
        NBTTagCompound times = new NBTTagCompound();
        for (Map.Entry<UUID, Map<ResourceLocation, Long>> playerEntry : prayerTimes.entrySet()) {
            NBTTagCompound playerTag = new NBTTagCompound();
            for (Map.Entry<ResourceLocation, Long> spellEntry : playerEntry.getValue().entrySet()) {
                playerTag.setLong(spellEntry.getKey().toString(), spellEntry.getValue());
            }
            times.setTag(playerEntry.getKey().toString(), playerTag);
        }
        compound.setTag("reps", reps);
        compound.setTag("times", times);
        return compound;
    }
}

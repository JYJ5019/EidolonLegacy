package elucent.eidolon.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ReputationEntry {
    private double reputation;
    private ResourceLocation lock;

    public double getReputation() {
        return reputation;
    }

    public void setReputation(double reputation) {
        this.reputation = reputation;
    }

    public ResourceLocation getLock() {
        return lock;
    }

    public void setLock(ResourceLocation lock) {
        this.lock = lock;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("rep", reputation);
        if (lock != null) {
            tag.setString("lock", lock.toString());
        }
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        reputation = tag.getDouble("rep");
        lock = tag.hasKey("lock") ? new ResourceLocation(tag.getString("lock")) : null;
    }
}

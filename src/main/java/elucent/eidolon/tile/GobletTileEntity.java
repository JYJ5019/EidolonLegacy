package elucent.eidolon.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class GobletTileEntity extends TileEntity {
    public static final ResourceLocation PLAYER = new ResourceLocation("minecraft", "player");

    private ResourceLocation entityType;

    public ResourceLocation getEntityType() {
        return entityType;
    }

    public void setEntityType(ResourceLocation entityType) {
        this.entityType = entityType;
        markDirty();
        notifyStateChanged();
    }

    public void clearEntityType() {
        setEntityType(null);
    }

    public boolean hasEntityType() {
        return entityType != null;
    }

    public boolean isAnimalSacrifice(World world) {
        Entity entity = createStoredEntity(world);
        return entity instanceof EntityAnimal;
    }

    public boolean isVillagerOrPlayerSacrifice(World world) {
        if (PLAYER.equals(entityType)) {
            return true;
        }
        Entity entity = createStoredEntity(world);
        return entity instanceof EntityVillager;
    }

    private Entity createStoredEntity(World world) {
        if (entityType == null || PLAYER.equals(entityType)) {
            return null;
        }
        return EntityList.createEntityByIDFromName(entityType, world);
    }

    private void notifyStateChanged() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (entityType != null) {
            compound.setString("type", entityType.toString());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("type", Constants.NBT.TAG_STRING)) {
            entityType = new ResourceLocation(compound.getString("type"));
        } else if (compound.hasKey("EntityType", Constants.NBT.TAG_STRING)) {
            entityType = new ResourceLocation(compound.getString("EntityType"));
        } else {
            entityType = null;
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        if (world != null && world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}

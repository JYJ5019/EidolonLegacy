package elucent.eidolon.network;

import elucent.eidolon.spell.SignSequence;
import elucent.eidolon.spell.Spell;
import elucent.eidolon.spell.SpellHelper;
import elucent.eidolon.spell.Spells;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SpellCastPacket implements IMessage {
    private UUID casterId;
    private BlockPos pos;
    private ResourceLocation spellId;
    private NBTTagCompound sequenceTag = new NBTTagCompound();

    public SpellCastPacket() {
    }

    public SpellCastPacket(EntityPlayer caster, BlockPos pos, Spell spell, SignSequence sequence) {
        this.casterId = caster == null ? null : caster.getUniqueID();
        this.pos = pos;
        this.spellId = spell == null ? null : spell.getRegistryName();
        this.sequenceTag = sequence == null ? new NBTTagCompound() : sequence.serializeNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        casterId = new UUID(buf.readLong(), buf.readLong());
        pos = BlockPos.fromLong(buf.readLong());
        spellId = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        sequenceTag = ByteBufUtils.readTag(buf);
        if (sequenceTag == null) {
            sequenceTag = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        UUID id = casterId == null ? new UUID(0L, 0L) : casterId;
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
        buf.writeLong(pos == null ? BlockPos.ORIGIN.toLong() : pos.toLong());
        ByteBufUtils.writeUTF8String(buf, spellId == null ? "" : spellId.toString());
        ByteBufUtils.writeTag(buf, sequenceTag == null ? new NBTTagCompound() : sequenceTag);
    }

    public static class Handler implements IMessageHandler<SpellCastPacket, IMessage> {
        @Override
        public IMessage onMessage(SpellCastPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(message::handleClient);
            return null;
        }
    }

    private void handleClient() {
        World world = Minecraft.getMinecraft().world;
        if (world == null || spellId == null) {
            return;
        }
        EntityPlayer caster = casterId == null ? null : world.getPlayerEntityByUUID(casterId);
        Spell spell = Spells.find(spellId);
        SignSequence sequence = SignSequence.deserializeNBT(sequenceTag);
        if (spell == null || sequence == null || pos == null) {
            return;
        }
        SpellHelper.playSpellCastVisuals(world, pos, caster, spell, sequence);
    }
}

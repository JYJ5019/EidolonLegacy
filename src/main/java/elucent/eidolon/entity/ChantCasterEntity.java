package elucent.eidolon.entity;

import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SpellCastPacket;
import elucent.eidolon.registries.ModSounds;
import elucent.eidolon.spell.Chanting;
import elucent.eidolon.spell.Rune;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.SignSequence;
import elucent.eidolon.spell.SignSequence.AverageColor;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChantCasterEntity extends Entity {
    private static final DataParameter<String> SIGNS =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> RUNES =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> INDEX =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SUCCEEDED =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> LOOK_X =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LOOK_Y =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LOOK_Z =
            EntityDataManager.createKey(ChantCasterEntity.class, DataSerializers.FLOAT);

    private final List<Rune> runes = new ArrayList<>();
    private SignSequence sequence = new SignSequence();
    private UUID casterId;
    private int timer;
    private int deathTimer;
    private int lastParticleIndex;

    public ChantCasterEntity(World worldIn) {
        super(worldIn);
        setSize(0.1F, 0.1F);
        noClip = true;
    }

    public ChantCasterEntity(World worldIn, EntityPlayer caster, List<Rune> runes, Vec3d look) {
        this(worldIn);
        this.casterId = caster.getUniqueID();
        this.runes.addAll(runes);
        setLook(look);
        setPosition(caster.posX + look.x * 0.8D,
                caster.posY + caster.getEyeHeight() + look.y * 0.35D,
                caster.posZ + look.z * 0.8D);
        syncRunes();
        syncSequence();
    }

    @Override
    protected void entityInit() {
        dataManager.register(SIGNS, "");
        dataManager.register(RUNES, "");
        dataManager.register(INDEX, 0);
        dataManager.register(SUCCEEDED, false);
        dataManager.register(LOOK_X, 0.0F);
        dataManager.register(LOOK_Y, 0.0F);
        dataManager.register(LOOK_Z, 1.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;

        if (world.isRemote) {
            playSyncedRuneSteps();
            return;
        }
        if (deathTimer > 0) {
            deathTimer--;
            if (deathTimer <= 0) {
                setDead();
            }
            return;
        }
        if (timer > 0) {
            timer--;
            if (timer <= 0) {
                finishChant();
            }
            return;
        }
        if (ticksExisted % 5 == 0) {
            applyNextRune();
        }
    }

    private void applyNextRune() {
        int index = dataManager.get(INDEX);
        if (index >= runes.size()) {
            timer = 20;
            return;
        }
        Rune rune = runes.get(index);
        EntityPlayer caster = getCaster();
        if (rune == null || caster == null
                || (!caster.capabilities.isCreativeMode && !KnowledgeUtil.knowsRune(caster, rune))) {
            failChant();
            return;
        }
        if (rune.doEffect(sequence) == Rune.RuneResult.FAIL) {
            failChant();
            return;
        }

        AverageColor color = sequence.getAverageColor();
        dataManager.set(INDEX, index + 1);
        syncSequence();
        playRuneStep(rune, color);
        if (index + 1 >= runes.size()) {
            timer = 20;
        }
    }

    private void finishChant() {
        EntityPlayer caster = getCaster();
        if (caster == null) {
            failChant();
            return;
        }
        Chanting.Result result = Chanting.castSigns(world, getPosition(), caster, sequence);
        dataManager.set(SUCCEEDED, result.isSuccess());
        if (result.isSuccess()) {
            ModNetwork.CHANNEL.sendToAllAround(new SpellCastPacket(caster, getPosition(), result.getSpell(), result.getSequence()),
                    new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 48.0D));
        }
        deathTimer = 20;
    }

    private void failChant() {
        dataManager.set(SUCCEEDED, false);
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.PLAYERS, 0.85F, 0.85F + rand.nextFloat() * 0.25F);
        deathTimer = 20;
    }

    private void playRuneStep(Rune rune, AverageColor color) {
        world.playSound(null, posX, posY, posZ, ModSounds.CHANT_WORD, SoundCategory.PLAYERS,
                0.7F, 0.625F + rand.nextFloat() * 0.375F);
    }

    private void playSyncedRuneSteps() {
        List<Rune> visibleRunes = decodeRunes(dataManager.get(RUNES));
        int index = Math.min(dataManager.get(INDEX), visibleRunes.size());
        if (index < lastParticleIndex) {
            lastParticleIndex = index;
        }
        while (lastParticleIndex < index) {
            spawnRuneStepParticle(visibleRunes, lastParticleIndex);
            lastParticleIndex++;
        }
    }

    private void spawnRuneStepParticle(List<Rune> visibleRunes, int stepIndex) {
        SignSequence before = new SignSequence();
        for (int i = 0; i < stepIndex && i < visibleRunes.size(); i++) {
            if (visibleRunes.get(i).doEffect(before) == Rune.RuneResult.FAIL) {
                return;
            }
        }
        if (stepIndex < 0 || stepIndex >= visibleRunes.size()) {
            return;
        }
        Rune rune = visibleRunes.get(stepIndex);
        AverageColor start = before.getAverageColor();
        if (rune.doEffect(before) == Rune.RuneResult.FAIL) {
            return;
        }
        AverageColor end = before.getAverageColor();
        Vec3d look = getLookDirection();
        double x = posX + 0.1D * rand.nextGaussian();
        double y = posY + 0.1D * rand.nextGaussian();
        double z = posZ + 0.1D * rand.nextGaussian();
        for (int i = 0; i < 2; i++) {
            EidolonParticles.spawnRune(world, rune, x, y, z,
                    look.x * 0.03D, look.y * 0.03D, look.z * 0.03D,
                    start.red, start.green, start.blue, end.red, end.green, end.blue);
        }
    }

    private EntityPlayer getCaster() {
        return casterId == null ? null : world.getPlayerEntityByUUID(casterId);
    }

    public SignSequence getSignSequence() {
        return decodeSigns(dataManager.get(SIGNS));
    }

    public Vec3d getLookDirection() {
        Vec3d look = new Vec3d(dataManager.get(LOOK_X), dataManager.get(LOOK_Y), dataManager.get(LOOK_Z));
        return look.length() <= 0.001D ? new Vec3d(0.0D, 0.0D, 1.0D) : look.normalize();
    }

    public int getDeathTimer() {
        return deathTimer;
    }

    public boolean hasSucceeded() {
        return dataManager.get(SUCCEEDED);
    }

    private void setLook(Vec3d look) {
        Vec3d normalized = look.length() <= 0.001D ? new Vec3d(0.0D, 0.0D, 1.0D) : look.normalize();
        dataManager.set(LOOK_X, (float) normalized.x);
        dataManager.set(LOOK_Y, (float) normalized.y);
        dataManager.set(LOOK_Z, (float) normalized.z);
    }

    private void syncSequence() {
        dataManager.set(SIGNS, encodeSigns(sequence));
    }

    private void syncRunes() {
        dataManager.set(RUNES, encodeRunes(runes));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        runes.clear();
        NBTTagList runeList = compound.getTagList("Runes", Constants.NBT.TAG_STRING);
        for (int i = 0; i < runeList.tagCount(); i++) {
            Rune rune = Runes.find(new ResourceLocation(runeList.getStringTagAt(i)));
            if (rune != null) {
                runes.add(rune);
            }
        }
        syncRunes();
        sequence = SignSequence.deserializeNBT(compound.getCompoundTag("Signs"));
        dataManager.set(INDEX, compound.getInteger("Index"));
        timer = compound.getInteger("Timer");
        deathTimer = compound.getInteger("DeathTimer");
        dataManager.set(SUCCEEDED, compound.getBoolean("Succeeded"));
        if (compound.hasKey("Caster")) {
            casterId = UUID.fromString(compound.getString("Caster"));
        }
        setLook(new Vec3d(compound.getDouble("LookX"), compound.getDouble("LookY"), compound.getDouble("LookZ")));
        syncSequence();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        NBTTagList runeList = new NBTTagList();
        for (Rune rune : runes) {
            runeList.appendTag(new NBTTagString(rune.getRegistryName().toString()));
        }
        compound.setTag("Runes", runeList);
        compound.setTag("Signs", sequence.serializeNBT());
        compound.setInteger("Index", dataManager.get(INDEX));
        compound.setInteger("Timer", timer);
        compound.setInteger("DeathTimer", deathTimer);
        compound.setBoolean("Succeeded", dataManager.get(SUCCEEDED));
        if (casterId != null) {
            compound.setString("Caster", casterId.toString());
        }
        Vec3d look = getLookDirection();
        compound.setDouble("LookX", look.x);
        compound.setDouble("LookY", look.y);
        compound.setDouble("LookZ", look.z);
    }

    private static String encodeSigns(SignSequence sequence) {
        StringBuilder builder = new StringBuilder();
        for (Sign sign : sequence.getSigns()) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(sign.getRegistryName());
        }
        return builder.toString();
    }

    private static SignSequence decodeSigns(String encoded) {
        List<Sign> signs = new ArrayList<>();
        if (encoded != null && !encoded.isEmpty()) {
            String[] parts = encoded.split(",");
            for (String part : parts) {
                Sign sign = Signs.find(new ResourceLocation(part));
                if (sign != null) {
                    signs.add(sign);
                }
            }
        }
        return new SignSequence(signs);
    }

    private static String encodeRunes(List<Rune> runes) {
        StringBuilder builder = new StringBuilder();
        for (Rune rune : runes) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(rune.getRegistryName());
        }
        return builder.toString();
    }

    private static List<Rune> decodeRunes(String encoded) {
        List<Rune> runes = new ArrayList<>();
        if (encoded != null && !encoded.isEmpty()) {
            String[] parts = encoded.split(",");
            for (String part : parts) {
                Rune rune = Runes.find(new ResourceLocation(part));
                if (rune != null) {
                    runes.add(rune);
                }
            }
        }
        return runes;
    }
}

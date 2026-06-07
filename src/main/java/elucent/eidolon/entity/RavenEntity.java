package elucent.eidolon.entity;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashSet;
import java.util.Set;

public class RavenEntity extends EntityParrot {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(Reference.MOD_ID, "entities/raven");
    private static final Set<Item> TEMPTATION_ITEMS = new HashSet<>();

    static {
        TEMPTATION_ITEMS.add(Items.RABBIT);
        TEMPTATION_ITEMS.add(Items.BEETROOT_SEEDS);
    }

    private int featherTime;

    public RavenEntity(World world) {
        super(world);
        setSize(0.375F, 0.5F);
        featherTime = rand.nextInt(12000) + 12000;
    }

    @Override
    protected void initEntityAI() {
        aiSit = new EntityAISit(this);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        tasks.addTask(2, aiSit);
        tasks.addTask(2, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
        tasks.addTask(2, new EntityAIMate(this, 1.0D, RavenEntity.class));
        tasks.addTask(3, new EntityAITempt(this, 1.0D, false, TEMPTATION_ITEMS));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        tasks.addTask(4, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAILandOnOwnersShoulder(this));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote && isEntityAlive() && !isChild() && --featherTime <= 0) {
            playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            entityDropItem(new ItemStack(ModItems.RAVEN_FEATHER), 0.0F);
            featherTime = rand.nextInt(12000) + 12000;
        }
        if (!onGround && motionY < 0.0D) {
            motionY *= 0.6D;
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(0.0F, damageMultiplier);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!isTamed() && isBreedingItem(stack)) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            if (!world.isRemote) {
                if (rand.nextInt(10) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                    setTamedBy(player);
                    playTameEffect(true);
                    world.setEntityState(this, (byte) 7);
                } else {
                    playTameEffect(false);
                    world.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        if (onGround && isTamed() && isOwner(player)) {
            if (!world.isRemote) {
                boolean sitting = !isSitting();
                aiSit.setSitting(sitting);
                setSitting(sitting);
                setAttackTarget(null);
            }
            return true;
        }
        if (isBreedingItem(stack) && canEatForBreeding()) {
            if (!world.isRemote) {
                consumeItemFromStack(player, stack);
                setInLove(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.BEETROOT_SEEDS;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return otherAnimal instanceof RavenEntity && otherAnimal != this
                && isInLove() && otherAnimal.isInLove();
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new RavenEntity(world);
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (!(entityIn instanceof EntityPlayer)) {
            super.collideWithEntity(entityIn);
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int feathers = rand.nextInt(3) + randomLootingBonus(lootingModifier, 1);
        if (feathers > 0) {
            entityDropItem(new ItemStack(ModItems.RAVEN_FEATHER, feathers), 0.0F);
        }
    }

    private int randomLootingBonus(int lootingModifier, int maxPerLevel) {
        return lootingModifier <= 0 || maxPerLevel <= 0 ? 0 : rand.nextInt(lootingModifier * maxPerLevel + 1);
    }

    private boolean canEatForBreeding() {
        return isTamed() && !isChild() && !isInLove();
    }
}

package elucent.eidolon.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class CleavingAxeItem extends ItemAxe {
    public CleavingAxeItem(ToolMaterial material) {
        super(material, 9.0F, -3.2F);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target.world.isRemote) {
            return;
        }
        DamageSource source = event.getSource();
        if (source == null || !(source.getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        if (!(attacker.getHeldItemMainhand().getItem() instanceof CleavingAxeItem)) {
            return;
        }
        ItemStack head = getHeadFor(target);
        if (head.isEmpty() || hasDuplicateHead(event, head) || !shouldDropHead(target, event.getLootingLevel())) {
            return;
        }
        EntityItem drop = new EntityItem(target.world, target.posX, target.posY, target.posZ, head);
        drop.setDefaultPickupDelay();
        event.getDrops().add(drop);
    }

    private boolean shouldDropHead(EntityLivingBase target, int looting) {
        if (target.world.rand.nextInt(20) == 0) {
            return true;
        }
        for (int i = 0; i < looting; i++) {
            if (target.world.rand.nextInt(40) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateHead(LivingDropsEvent event, ItemStack head) {
        for (EntityItem drop : event.getDrops()) {
            if (drop.getItem().isItemEqual(head)) {
                return true;
            }
        }
        return false;
    }

    private ItemStack getHeadFor(EntityLivingBase target) {
        if (target instanceof EntityWitherSkeleton) {
            return new ItemStack(Items.SKULL, 1, 1);
        }
        if (target instanceof EntitySkeleton) {
            return new ItemStack(Items.SKULL, 1, 0);
        }
        if (target instanceof EntityZombie) {
            return new ItemStack(Items.SKULL, 1, 2);
        }
        if (target instanceof EntityCreeper) {
            return new ItemStack(Items.SKULL, 1, 4);
        }
        if (target instanceof EntityDragon) {
            return new ItemStack(Items.SKULL, 1, 5);
        }
        if (target instanceof EntityPlayer) {
            ItemStack head = new ItemStack(Items.SKULL, 1, 3);
            NBTTagCompound owner = new NBTTagCompound();
            NBTUtil.writeGameProfile(owner, ((EntityPlayer) target).getGameProfile());
            head.setTagInfo("SkullOwner", owner);
            return head;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.ITALIC
                + I18n.translateToLocal("lore.eidolon.cleaving_axe"));
    }
}

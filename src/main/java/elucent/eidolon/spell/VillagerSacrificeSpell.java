package elucent.eidolon.spell;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.deity.Deity;
import elucent.eidolon.deity.DeityLocks;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VillagerSacrificeSpell extends StaticSpell {
    private final Deity deity;

    public VillagerSacrificeSpell(net.minecraft.util.ResourceLocation registryName, Deity deity, Sign... signs) {
        super(registryName, signs);
        this.deity = deity;
    }

    @Override
    public boolean canCast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return false;
        }
        ReputationData reputation = ReputationData.get(world);
        EffigyTileEntity effigy = SpellHelper.findUnholyEffigyTile(world, pos);
        GobletTileEntity goblet = SpellHelper.findGoblet(world, pos);
        return reputation.canPray(player, getRegistryName(), world.getTotalWorldTime())
                && reputation.getReputation(player, deity.getId()) >= 15.0D
                && effigy != null
                && SpellHelper.isOnStoneAltar(world, effigy.getPos())
                && effigy.ready()
                && goblet != null
                && goblet.isVillagerOrPlayerSacrifice(world);
    }

    @Override
    public void cast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        EffigyTileEntity effigy = SpellHelper.findUnholyEffigyTile(world, pos);
        GobletTileEntity goblet = SpellHelper.findGoblet(world, pos);
        if (effigy == null
                || !SpellHelper.isOnStoneAltar(world, effigy.getPos())
                || goblet == null
                || !goblet.isVillagerOrPlayerSacrifice(world)) {
            return;
        }

        effigy.pray();
        goblet.clearEntityType();
        ReputationData reputation = ReputationData.get(world);
        reputation.pray(player, getRegistryName(), world.getTotalWorldTime());
        reputation.unlock(player, deity.getId(), DeityLocks.SACRIFICE_VILLAGER);
        BlockPos effigyPos = effigy.getPos();
        reputation.addReputation(player, deity.getId(), 6.0D + SpellHelper.getNearbyAltarPower(world, effigyPos));
        SpellHelper.playChantSuccess(world, effigyPos, Signs.SOUL_SIGN, Signs.BLOOD_SIGN);
    }
}

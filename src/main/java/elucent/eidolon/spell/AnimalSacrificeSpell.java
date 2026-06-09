package elucent.eidolon.spell;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.deity.Deity;
import elucent.eidolon.deity.DeityLocks;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnimalSacrificeSpell extends StaticSpell {
    private final Deity deity;

    public AnimalSacrificeSpell(net.minecraft.util.ResourceLocation registryName, Deity deity, Sign... signs) {
        super(registryName, signs);
        this.deity = deity;
    }

    @Override
    public boolean canCast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return false;
        }
        ReputationData reputation = ReputationData.get(world);
        EffigyTileEntity effigy = SpellHelper.findEffigyTile(world, pos);
        GobletTileEntity goblet = SpellHelper.findGoblet(world, pos);
        return reputation.canPray(player, getRegistryName(), world.getTotalWorldTime())
                && reputation.getReputation(player, deity.getId()) >= 3.0D
                && effigy != null
                && effigy.ready()
                && goblet != null
                && goblet.isAnimalSacrifice(world);
    }

    @Override
    public void cast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        EffigyTileEntity effigy = SpellHelper.findEffigyTile(world, pos);
        GobletTileEntity goblet = SpellHelper.findGoblet(world, pos);
        if (effigy == null || goblet == null || !goblet.isAnimalSacrifice(world)) {
            return;
        }

        effigy.pray();
        goblet.clearEntityType();
        ReputationData reputation = ReputationData.get(world);
        reputation.pray(player, getRegistryName(), world.getTotalWorldTime());
        reputation.unlock(player, deity.getId(), DeityLocks.SACRIFICE_MOB);
        BlockPos effigyPos = effigy.getPos();
        reputation.addReputation(player, deity.getId(), 3.0D + 0.5D * SpellHelper.getNearbyAltarPower(world, effigyPos));
        SpellHelper.playChantSuccess(world, effigyPos);
    }
}

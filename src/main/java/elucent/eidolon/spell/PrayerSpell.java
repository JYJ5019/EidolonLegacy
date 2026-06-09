package elucent.eidolon.spell;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.deity.Deity;
import elucent.eidolon.tile.EffigyTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrayerSpell extends StaticSpell {
    private final Deity deity;

    public PrayerSpell(net.minecraft.util.ResourceLocation registryName, Deity deity, Sign... signs) {
        super(registryName, signs);
        this.deity = deity;
    }

    Deity getDeity() {
        return deity;
    }

    @Override
    public boolean canCast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return false;
        }
        ReputationData reputation = ReputationData.get(world);
        EffigyTileEntity effigy = SpellHelper.findEffigyTile(world, pos);
        return reputation.canPray(player, getRegistryName(), world.getTotalWorldTime())
                && effigy != null
                && effigy.ready();
    }

    @Override
    public void cast(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        EffigyTileEntity effigy = SpellHelper.findEffigyTile(world, pos);
        if (effigy == null) {
            return;
        }
        effigy.pray();
        ReputationData reputation = ReputationData.get(world);
        reputation.pray(player, getRegistryName(), world.getTotalWorldTime());
        BlockPos effigyPos = effigy.getPos();
        reputation.addReputation(player, deity.getId(), 1.0D + 0.25D * SpellHelper.getNearbyAltarPower(world, effigyPos));
        SpellHelper.playChantSuccess(world, effigyPos);
    }
}

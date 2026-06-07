package elucent.eidolon.reagent;

import net.minecraft.util.EnumFacing;

public interface IReagentTankProvider {
    ReagentTank getTank();

    boolean isOutput(EnumFacing direction);

    boolean isInput(EnumFacing direction);

    void onContentsChanged();
}

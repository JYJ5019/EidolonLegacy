package elucent.eidolon.reagent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class ReagentStack {
    public Reagent reagent;
    public int amount;

    public ReagentStack(Reagent reagent, int amount) {
        this.reagent = reagent;
        this.amount = Math.max(0, amount);
    }

    public ReagentStack(NBTTagCompound compound) {
        String name = compound.hasKey("Reagent", Constants.NBT.TAG_STRING)
                ? compound.getString("Reagent") : ReagentRegistry.STEAM.getRegistryName().toString();
        Reagent found = ReagentRegistry.find(new ResourceLocation(name));
        this.reagent = found == null ? ReagentRegistry.STEAM : found;
        this.amount = Math.max(0, compound.getInteger("Amount"));
    }

    public boolean isEmpty() {
        return reagent == null || amount <= 0;
    }

    public ReagentStack copy() {
        return new ReagentStack(reagent, amount);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Reagent", (reagent == null ? ReagentRegistry.STEAM : reagent).getRegistryName().toString());
        compound.setInteger("Amount", amount);
        return compound;
    }
}

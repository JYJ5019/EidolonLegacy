package elucent.eidolon.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class ReinforcedPotion extends EidolonPotion {
    public ReinforcedPotion() {
        super(false, 0xfad64a, "reinforced");
        setIconIndex(3, 0);
        setBeneficial();
    }

    public void registerAttributeModifiers() {
        registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR,
                "483b6415-421e-45d1-ab28-d85d11a19c70", 0.25D, 2);
    }
}

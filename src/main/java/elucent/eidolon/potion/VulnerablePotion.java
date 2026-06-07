package elucent.eidolon.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class VulnerablePotion extends EidolonPotion {
    public VulnerablePotion() {
        super(true, 0x5a66a1, "vulnerable");
        setIconIndex(4, 0);
    }

    public void registerAttributeModifiers() {
        registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR,
                "e5bae4de-2019-4316-b8cc-b4d879d676f9", -0.25D, 2);
    }
}

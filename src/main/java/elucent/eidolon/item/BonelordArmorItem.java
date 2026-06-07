package elucent.eidolon.item;

import net.minecraft.inventory.EntityEquipmentSlot;

public class BonelordArmorItem extends EidolonArmorItem {
    public BonelordArmorItem(ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "bonelord", "tooltip.eidolon.bonelord_armor");
    }

    public int getPersistentEtherealHealth() {
        return armorType == EntityEquipmentSlot.CHEST ? 20 : 10;
    }
}

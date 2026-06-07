package elucent.eidolon.item;

import net.minecraft.inventory.EntityEquipmentSlot;

public class WardedMailItem extends EidolonCurioArmorItem {
    public WardedMailItem(ArmorMaterial material) {
        super(material, EntityEquipmentSlot.CHEST, "warded", "tooltip.eidolon.warded_mail");
    }
}

package elucent.eidolon.item;

import net.minecraft.inventory.EntityEquipmentSlot;

public class TopHatItem extends EidolonArmorItem {
    public TopHatItem(ArmorMaterial material) {
        super(material, EntityEquipmentSlot.HEAD, "top_hat", "lore.eidolon.top_hat");
    }
}

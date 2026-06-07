package elucent.eidolon.mixin;

import net.minecraft.entity.monster.EntityZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityZombieVillager.class)
public interface EntityZombieVillagerMixin {
    @Invoker("finishConversion")
    void eidolon$finishConversion();
}

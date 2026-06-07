package elucent.eidolon.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.ResourceLocation;

public interface IProxy {
    default void preInit(FMLPreInitializationEvent event) {
    }

    default void init(FMLInitializationEvent event) {
    }

    default void postInit(FMLPostInitializationEvent event) {
    }

    default void syncKnownResearchClient(ResourceLocation research, boolean known) {
    }

    default void syncMagicKnowledgeClient(int type, ResourceLocation id, boolean known) {
    }

    default void resetKnowledgeClient() {
    }

    default void syncSoulClient(float maxMagic, float magic, float maxEtherealHealth, float etherealHealth) {
    }
}

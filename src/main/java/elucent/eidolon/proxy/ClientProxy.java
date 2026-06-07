package elucent.eidolon.proxy;

import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.client.RavenCloakControls;
import elucent.eidolon.client.render.AltarTileEntityRenderer;
import elucent.eidolon.client.render.AngelArrowRenderer;
import elucent.eidolon.client.render.BrazierTileEntityRenderer;
import elucent.eidolon.client.render.ChantCasterRenderer;
import elucent.eidolon.client.render.CisternTileEntityRenderer;
import elucent.eidolon.client.render.CrucibleTileEntityRenderer;
import elucent.eidolon.client.render.GlassTubeTileEntityRenderer;
import elucent.eidolon.client.render.InvisibleEntityRenderer;
import elucent.eidolon.client.render.ItemHolderTileEntityRenderer;
import elucent.eidolon.client.render.NecromancerRenderer;
import elucent.eidolon.client.render.NecroticFocusTileEntityRenderer;
import elucent.eidolon.client.render.OffertoryPlateTileEntityRenderer;
import elucent.eidolon.client.render.RavenRenderer;
import elucent.eidolon.client.render.SlimySlugRenderer;
import elucent.eidolon.client.render.SoulEnchanterTileEntityRenderer;
import elucent.eidolon.client.render.WraithRenderer;
import elucent.eidolon.client.render.ZombieBruteRenderer;
import elucent.eidolon.client.render.shader.LegacyShaders;
import elucent.eidolon.entity.AngelArrowEntity;
import elucent.eidolon.entity.BonechillProjectileEntity;
import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.entity.NecromancerEntity;
import elucent.eidolon.entity.NecromancerSpellEntity;
import elucent.eidolon.entity.RavenEntity;
import elucent.eidolon.entity.SlimySlugEntity;
import elucent.eidolon.entity.SoulfireProjectileEntity;
import elucent.eidolon.entity.WraithEntity;
import elucent.eidolon.entity.ZombieBruteEntity;
import elucent.eidolon.capability.SoulData;
import elucent.eidolon.network.MagicKnowledgeSyncPacket;
import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.tile.AltarTileEntity;
import elucent.eidolon.tile.BrazierTileEntity;
import elucent.eidolon.tile.CisternTileEntity;
import elucent.eidolon.tile.CrucibleTileEntity;
import elucent.eidolon.tile.GlassTubeTileEntity;
import elucent.eidolon.tile.NecroticFocusTileEntity;
import elucent.eidolon.tile.OffertoryPlateTileEntity;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import elucent.eidolon.tile.StoneHandTileEntity;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientConfig.init(event.getSuggestedConfigurationFile());
        EidolonParticles.registerDefaults();
        MinecraftForge.EVENT_BUS.register(new EidolonParticles.TextureEvents());
        ClientRegistry.bindTileEntitySpecialRenderer(CrucibleTileEntity.class, new CrucibleTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CisternTileEntity.class, new CisternTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(GlassTubeTileEntity.class, new GlassTubeTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(AltarTileEntity.class, new AltarTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(BrazierTileEntity.class, new BrazierTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(StoneHandTileEntity.class, new ItemHolderTileEntityRenderer<>());
        ClientRegistry.bindTileEntitySpecialRenderer(NecroticFocusTileEntity.class, new NecroticFocusTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(OffertoryPlateTileEntity.class, new OffertoryPlateTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(SoulEnchanterTileEntity.class, new SoulEnchanterTileEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(SoulfireProjectileEntity.class, InvisibleEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BonechillProjectileEntity.class, InvisibleEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(NecromancerSpellEntity.class, InvisibleEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AngelArrowEntity.class, AngelArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ChantCasterEntity.class, ChantCasterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(WraithEntity.class, WraithRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ZombieBruteEntity.class, ZombieBruteRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(NecromancerEntity.class, NecromancerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RavenEntity.class, RavenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SlimySlugEntity.class, SlimySlugRenderer::new);
        RavenCloakControls.init();
        LegacyShaders.registerReloadListener();
    }

    @Override
    public void syncKnownResearchClient(ResourceLocation research, boolean known) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player != null) {
                KnowledgeUtil.setResearchKnown(Minecraft.getMinecraft().player, research, known);
            }
        });
    }

    @Override
    public void syncMagicKnowledgeClient(int type, ResourceLocation id, boolean known) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player == null) {
                return;
            }
            if (type == MagicKnowledgeSyncPacket.SIGN) {
                KnowledgeUtil.setSignKnown(Minecraft.getMinecraft().player, id, known);
            } else if (type == MagicKnowledgeSyncPacket.RUNE) {
                KnowledgeUtil.setRuneKnown(Minecraft.getMinecraft().player, id, known);
            } else if (type == MagicKnowledgeSyncPacket.FACT) {
                KnowledgeUtil.setFactKnown(Minecraft.getMinecraft().player, id, known);
            }
        });
    }

    @Override
    public void resetKnowledgeClient() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player == null) {
                return;
            }
            KnowledgeUtil.clearResearch(Minecraft.getMinecraft().player);
            KnowledgeUtil.clearSigns(Minecraft.getMinecraft().player);
            KnowledgeUtil.clearRunes(Minecraft.getMinecraft().player);
            KnowledgeUtil.clearFacts(Minecraft.getMinecraft().player);
        });
    }

    @Override
    public void syncSoulClient(float maxMagic, float magic, float maxEtherealHealth, float etherealHealth) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player == null) {
                return;
            }
            SoulData.setMaxMagic(Minecraft.getMinecraft().player, maxMagic);
            SoulData.setMagic(Minecraft.getMinecraft().player, magic);
            SoulData.setMaxEtherealHealth(Minecraft.getMinecraft().player, maxEtherealHealth);
            SoulData.setEtherealHealth(Minecraft.getMinecraft().player, etherealHealth);
        });
    }
}

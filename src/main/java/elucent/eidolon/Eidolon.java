package elucent.eidolon;

import elucent.eidolon.command.LocateStructureCommand;
import elucent.eidolon.command.ResearchCommand;
import elucent.eidolon.command.SpellCommand;
import elucent.eidolon.diagnostics.RuntimeDiagnostics;
import elucent.eidolon.proxy.IProxy;
import elucent.eidolon.registries.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class Eidolon {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);
    public static final DamageSource RITUAL_DAMAGE = new DamageSource("ritual")
            .setDamageBypassesArmor()
            .setDamageIsAbsolute();
    public static final DamageSource FROST_DAMAGE = new DamageSource("frost");

    @Mod.Instance(Reference.MOD_ID)
    public static Eidolon instance;

    @SidedProxy(modId = Reference.MOD_ID, clientSide = "elucent.eidolon.proxy.ClientProxy", serverSide = "elucent.eidolon.proxy.CommonProxy")
    public static IProxy proxy;

    public static EnumCreatureAttribute getCreatureAttribute(EntityLivingBase entity) {
        if (entity != null && entity.isPotionActive(ModPotions.UNDEATH)) {
            return EnumCreatureAttribute.UNDEAD;
        }
        return getTrueCreatureAttribute(entity);
    }

    public static EnumCreatureAttribute getTrueCreatureAttribute(EntityLivingBase entity) {
        return entity == null ? EnumCreatureAttribute.UNDEFINED : entity.getCreatureAttribute();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("{} preInit", Reference.MOD_NAME);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("{} init", Reference.MOD_NAME);
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("{} postInit", Reference.MOD_NAME);
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ResearchCommand());
        event.registerServerCommand(new SpellCommand());
        event.registerServerCommand(new LocateStructureCommand());
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        RuntimeDiagnostics.runIfRequested(FMLCommonHandler.instance().getMinecraftServerInstance());
    }

}

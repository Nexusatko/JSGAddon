package nex.jsgaddon;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import nex.jsgaddon.command.CommandRegistry;
import nex.jsgaddon.event.Chat;
import nex.jsgaddon.event.Tick;
import nex.jsgaddon.loader.FromFile;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(
        modid = JSGAddon.MOD_ID,
        name = JSGAddon.MOD_NAME,
        version = JSGAddon.VERSION,
        acceptableRemoteVersions = "*"
)

public class JSGAddon {

    public static final String MOD_ID = "jsgaddon";
    public static final String MOD_NAME = "JSGAddon";
    public static final String VERSION = "1.1";

    public static Logger LOGGER;

    public static void info(String s) {
        LOGGER.info(s);
    }

    public static void error(String s) {
        LOGGER.error(s);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        FromFile.load(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!Loader.isModLoaded("jsg")) {
            JSGAddon.error("JSG Mod is not loaded!!!");
            Minecraft.getMinecraft().shutdown();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(new Tick());
        MinecraftForge.EVENT_BUS.register(new Chat());
        CommandRegistry.register(event);
        info("Commands registered!");
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) throws IOException {
        FromFile.update();
    }


}

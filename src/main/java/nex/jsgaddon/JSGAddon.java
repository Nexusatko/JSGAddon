package nex.jsgaddon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import nex.jsgaddon.command.CommandRegistry;
import nex.jsgaddon.event.Chat;
import nex.jsgaddon.event.Tick;
import nex.jsgaddon.loader.FromFile;
import nex.jsgaddon.proxy.IProxy;
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
    public static final String VERSION = "@VERSION@";

    public static Logger logger;

    public static final String CLIENT = "nex.jsgaddon.proxy.ProxyClient";
    public static final String SERVER = "nex.jsgaddon.proxy.ProxyServer";


    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static IProxy proxy;

    public static void info(String s) {
        logger.info(s);
    }


    public static void error(String s) {
        logger.error(s);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        logger = event.getModLog();
        JSGAddon.proxy.preInit(event);


        Runtime.getRuntime().addShutdownHook(new Thread(JSGAddon::shutDown));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!Loader.isModLoaded("jsg")) {
            throw new LoaderException("JSG mod is required for JSGAddon.");
        }
        //JSGAddon.proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //JSGAddon.proxy.postInit(event);
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

    public static void shutDown() {
        //JSGAddon.proxy.shutDown();
        JSGAddon.info("I'm trying do prdele, where u going?!");
    }
}

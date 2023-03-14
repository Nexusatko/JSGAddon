package nex.jsgaddon.proxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
@SuppressWarnings("unused")

public interface IProxy {
    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    String localize(String unlocalized, Object... args);

    void shutDown();

    EntityPlayer getPlayerClientSide();

    void openGui(GuiScreen gui);
}

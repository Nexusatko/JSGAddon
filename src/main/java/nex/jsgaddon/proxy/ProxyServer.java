package nex.jsgaddon.proxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nex.jsgaddon.loader.FromFile;

@SuppressWarnings("unused")
public class ProxyServer implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FromFile.load(event.getModConfigurationDirectory());
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public String localize(String unlocalized, Object... args) {
        return net.minecraft.client.resources.I18n.format(unlocalized, args);
    }

    @Override
    public void shutDown() {

    }

    @Override
    public EntityPlayer getPlayerClientSide() {
        return null;
    }

    @Override
    public void openGui(GuiScreen gui) {

    }
}

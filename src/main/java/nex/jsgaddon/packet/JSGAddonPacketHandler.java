package nex.jsgaddon.packet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import nex.jsgaddon.JSGAddon;

@SuppressWarnings("unused")
public class JSGAddonPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(JSGAddon.MOD_ID);

    public static int id = 0;

    public static void registerPackets() {

    }
}

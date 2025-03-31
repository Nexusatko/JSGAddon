package nex.jsgaddon.command;

import com.forgeessentials.core.misc.PermissionManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import nex.jsgaddon.command.stargate.*;

import java.util.Arrays;
import java.util.List;

public class CommandRegistry {
    @SuppressWarnings("unused")
    public static final List<AbstractJSGACommand> COMMANDS = Arrays.asList(
            new CommandTest(),
            new AddAddress(),
            new RemoveAddress(),
            new CheckAddress(),
            new CloseCommand(),
            new GateControl(),
            new IrisBypassCommand(),
            new DialCommand(),
            new DialGatesCommand(),
            new AddressReloadCommand(),
            new TeleportCommand()
    );
    public static void register(FMLServerStartingEvent event) {
        event.registerServerCommand(JSGACommand.INSTANCE);
        if (Loader.isModLoaded("forgeessentials")) {
            PermissionManager.registerCommandPermission(JSGACommand.INSTANCE, "jsgaddon", DefaultPermissionLevel.ALL);
            for (AbstractJSGACommand cmd : COMMANDS) {
                PermissionAPI.registerNode("jsgaddon" + "." + cmd.getName(),
                        cmd.getRequiredPermissionLevel() >= 1 ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL,
                        cmd.getDescription());
            }
        }
    }
}

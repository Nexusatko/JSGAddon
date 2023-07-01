package nex.jsgaddon.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import nex.jsgaddon.command.stargate.*;

import java.util.Arrays;
import java.util.List;

public class CommandRegistry {
    @SuppressWarnings("unused")
    public static final List<AbstractJSGACommand> COMMANDS = Arrays.asList(
            new CommandTest(),
            new AddAddress(),
            new CheckAddress(),
            new CloseCommand(),
            new GateControl(),
            new IrisBypassCommand(),
            new NoxCommand(),
            new NoxReloadCommand()
    );

    public static void register(FMLServerStartingEvent event) {
        event.registerServerCommand(JSGACommand.INSTANCE);
    }
}

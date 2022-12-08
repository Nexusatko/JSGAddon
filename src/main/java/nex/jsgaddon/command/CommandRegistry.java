package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.Arrays;
import java.util.List;

public class CommandRegistry {
    public static final List<CommandBase> COMMANDS = Arrays.asList(
            new NoxCommand(),
            new IrisBypassCommand(),
            new NoxReloadCommand(),
            new CloseCommand(),
            new CheckAddress(),
            new AddAddress(),
            new GateControl()
    );

    public static void register(FMLServerStartingEvent event) {
        for (CommandBase cmd : COMMANDS)
            event.registerServerCommand(cmd);
    }
}

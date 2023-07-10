package nex.jsgaddon.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandTest extends AbstractJSGACommand {
    @Nonnull
    @Override
    public String getDescription() {
        return "Test command from JSGA";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "test <message>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Nonnull
    @Override
    public String getName() {
        return "test";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length > 0){
            StringBuilder sb = new StringBuilder();
            for(String s : args)
                sb.append(s).append(' ');
            baseCommand.sendInfoMess(sender, sb.toString().replace("&", "§"));
        }
        else{
            baseCommand.sendUsageMess(sender, this);
        }
    }
}

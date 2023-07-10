package nex.jsgaddon.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.FromFile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

public class NoxReloadCommand extends AbstractJSGACommand {
    @Override
    @Nonnull
    public String getName() {
        return "nox-reload";
    }
    @Nonnull
    @Override
    public String getDescription() {
        return "Reloads a address list from addresslist.json.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "nox-reload";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            FromFile.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ((JSGACommand) baseCommand).sendSuccessMess(sender,new TextComponentString("Reloaded!"));

    }
}

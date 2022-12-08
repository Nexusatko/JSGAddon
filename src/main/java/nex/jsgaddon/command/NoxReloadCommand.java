package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.FromFile;

public class NoxReloadCommand extends CommandBase {
    @Override
    public String getName() {
        return "nox-reload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/now-reload";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        FromFile.reload();
        sender.sendMessage(new TextComponentString("Reloaded!"));
    }
}

package nex.jsgaddon.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
@SuppressWarnings("unused")
public class Messages {
    public static void sendErrorMess(ICommandSender player, TextComponentString mess) {
        player.sendMessage(new TextComponentString(" §c§lOps! §7").appendSibling(mess));
    }
    public static void sendErrorMess(ICommandSender player, String mess) {
        player.sendMessage(new TextComponentString(" §c§lOps! §7" + mess));
    }

    public static void sendSuccessMess(ICommandSender player, TextComponentString mess) {
        player.sendMessage(new TextComponentString(" §a§lDone! §7").appendSibling(mess));
    }

    public static void sendSuccessMess(ICommandSender player, String mess) {
        player.sendMessage(new TextComponentString(" §a§lDone! §7" + mess));
    }

    public static void sendInfoMess(ICommandSender player, TextComponentString mess) {
        player.sendMessage(new TextComponentString("  §3§l│ §7").appendSibling(mess));
    }

    public static void sendInfoMess(ICommandSender player, String mess) {
        player.sendMessage(new TextComponentString(" §3§l│ §7" + mess));
    }

    public static void sendRunningMess(ICommandSender player, String mess) {
        player.sendMessage(new TextComponentString(" §6§lRunning: §7" + mess));
    }

    public static void sendRunningMess(ICommandSender player, TextComponentString mess) {
        player.sendMessage(new TextComponentString(" §6§lRunning: §7").appendSibling(mess));
    }
}

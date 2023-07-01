package nex.jsgaddon.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;

@SuppressWarnings("unused")
public class JSGACommand extends JSGCommand {

    public static final JSGACommand INSTANCE = new JSGACommand();

    public JSGACommand() {
        super("jsga");
    }

    public String getTitle(){
        return "JSGAddon";
    }


    public void sendNoPerms(ICommandSender sender) {
        this.sendErrorMess(sender, "You don't have permission to do that!");
    }

    public void sendErrorMess(ICommandSender sender, TextComponentString mess) {
        sender.sendMessage(new TextComponentString(" §c§lOps! §7").appendSibling(mess));
    }
    public void sendErrorMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" §c§lOps! §7" + mess));
    }

    public void sendSuccessMess(ICommandSender sender, TextComponentString mess) {
        sender.sendMessage(new TextComponentString(" §a§lDone! §7").appendSibling(mess));
    }

    public void sendSuccessMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" §a§lDone! §7" + mess));
    }

    public void sendInfoMess(ICommandSender sender, TextComponentString mess) {
        sender.sendMessage(new TextComponentString("  §3§l│ §7").appendSibling(mess));
    }

    public void sendInfoMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" §3§l│ §7" + mess));
    }

    public void sendUsageMess(ICommandSender sender, AbstractJSGCommand cmd) {
        sender.sendMessage(new TextComponentString(" §3§lUsage: §7/" + this.func_71517_b() + " " + cmd.func_71518_a(sender)));
    }

    public void sendRunningMess(ICommandSender sender, String mess) {
        sender.sendMessage(new TextComponentString(" §6§lRunning: §7" + mess));
    }

    public void sendRunningMess(ICommandSender sender, TextComponentString mess) {
        sender.sendMessage(new TextComponentString(" §6§lRunning: §7").appendSibling(mess));
    }
}

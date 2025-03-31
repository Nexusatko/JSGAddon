package nex.jsgaddon.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.server.permission.PermissionAPI;
import tauri.dev.jsg.command.AbstractJSGCommand;
import tauri.dev.jsg.command.JSGCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public class JSGACommand extends JSGCommand {

    public static final JSGACommand INSTANCE = new JSGACommand();
    private boolean permissionHandlerPresent() {
        return (Loader.isModLoaded("forgeessentials"));
    }
    public JSGACommand() {
        super("jsga");
    }
    public static final JSGCommand JSG_BASE_COMMAND = new JSGCommand("jsg");

    public static class CommandHelp extends AbstractJSGACommand {

        public CommandHelp(JSGACommand baseCommand) {
            super();
        }

        @Nonnull
        @Override
        public String getName() {
            return "help";
        }

        @Nonnull
        @Override
        public String getDescription() {
            return "Shows this list";
        }

        @Nonnull
        @Override
        public String getGeneralUsage() {
            return "help";
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (Exception ignored) {
                }
            }
            baseCommand.showHelp(sender, page);
        }
    }
    public String getTitle(){
        return "JSGAddon";
    }

    @Override
    @Nonnull
    public String getName() {
        return "jsga";
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

    @Override
    public void showHelp(ICommandSender sender, int page) {
        sender.sendMessage(new TextComponentString(TextFormatting.STRIKETHROUGH + "------" + TextFormatting.RESET + " " + TextFormatting.AQUA + TextFormatting.BOLD + getTitle() + " " + TextFormatting.RESET + TextFormatting.STRIKETHROUGH + "------"));

        ArrayList<AbstractJSGCommand> commands = new ArrayList<>();
        for (AbstractJSGCommand c : subCommands) {
            if (permissionHandlerPresent()) {
                if (PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + c.getName())) {
                    commands.add(c);
                }
            } else {
                if (canUseCommand(sender, c.getRequiredPermissionLevel())) {
                    commands.add(c);
                }
            }

        }

        int count = commands.size();
        final int perPage = 10;
        final int maxPage = (int) Math.ceil((double) count / perPage);
        page = Math.max(1, Math.min(maxPage, page));

        int start = perPage * (page - 1);
        int end = perPage * page;

        int i = 0;
        for (AbstractJSGCommand c : commands) {
            i++;
            if (i <= start) continue;
            if (i > end) break;
            sender.sendMessage(getCommandTextComponentForHelp(c));
        }

        TextComponentString back = (TextComponentString) new TextComponentString("§3§l§m<--§r").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + this.func_71517_b() + " help " + (page - 1))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Previous page"))));
        TextComponentString next = (TextComponentString) new TextComponentString("§3§l§m-->§r").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + this.func_71517_b() + " help " + (page + 1))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Next page"))));
        TextComponentString arrows = new TextComponentString("       ");
        if (page - 1 > 0)
            arrows.appendSibling(back);
        else
            arrows.appendSibling(new TextComponentString("§8§l§m<--§r"));
        arrows.appendSibling(new TextComponentString(" §7(" + page + "/" + maxPage + ")§r "));
        if (page + 1 <= maxPage)
            arrows.appendSibling(next);
        else
            arrows.appendSibling(new TextComponentString("§8§l§m-->§r"));
        sender.sendMessage(arrows);
        sender.sendMessage(new TextComponentString(TextFormatting.STRIKETHROUGH + "--------------------------------"));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int argsLength = args.length;
        if (argsLength == 0) {
            showHelp(sender, 1);
            return;
        }

        // Get subcommand
        AbstractJSGCommand command = null;
        for (AbstractJSGCommand c : subCommands) {
            if (c.getName().equalsIgnoreCase(args[0])) {
                command = c;
                break;
            }
        }
        if (command == null) {
            sendErrorMess(sender, "Unknown subcommand! Type /" + getName() + " help for help");
            return;
        }

        // Check permissions
        if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
            if (permissionHandlerPresent()) {
                if (!PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + command.getName())) {
                    sendNoPerms(sender);
                    return;
                }
            } else {
                if (!canUseCommand(sender, command.getRequiredPermissionLevel())) {
                    sendNoPerms(sender);
                    return;
                }
            }
        }


        // Remove first argument
        List<String> a = Arrays.asList(args);
        if (a.size() >= 2)
            a = a.subList(1, a.size());
        else
            a = new ArrayList<>();

        // Execute
        command.execute(server, sender, a.toArray(new String[0]));
    }

    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> list = new ArrayList<>();

        int argsLength = args.length;
        if (argsLength == 0) return list;
        if (argsLength == 1) {
            List<String> names = new ArrayList<>();
            for (AbstractJSGCommand c : subCommands) {
                if (permissionHandlerPresent()) {
                    if (PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + c.getName())) {
                        names.add(c.getName());
                    }
                } else {
                    if (canUseCommand(sender, c.getRequiredPermissionLevel())) {
                        names.add(c.getName());
                    }
                }
            }
            return getListOfStringsMatchingLastWord(args, names);
        }

        // Get subcommand
        AbstractJSGCommand command = null;
        for (AbstractJSGCommand c : subCommands) {
            if (c.getName().equalsIgnoreCase(args[0])) {
                command = c;
                break;
            }
        }
        if (command == null) {
            return list;
        }

        // Check permissions
        if (permissionHandlerPresent()) {
            if (!PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + command.getName())) {
                return list;
            }
        } else {
            if (!canUseCommand(sender, command.getRequiredPermissionLevel())) {
                return list;
            }
        }

        // Remove first argument
        List<String> a = Arrays.asList(args);
        a = a.subList(1, a.size());

        return command.getTabCompletions(server, sender, a.toArray(new String[0]), targetPos);
    }
}

package nex.jsgaddon.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nex.jsgaddon.JSGAddon;


public class Chat {
    @SubscribeEvent
    public void onChatEvent(ServerChatEvent chat) {
        if (chat.getMessage().equalsIgnoreCase("JSGAddon") || chat.getMessage().equalsIgnoreCase("nox")) {
            ITextComponent received = chat.getComponent();
            chat.setComponent(received.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("§eJSGAddon " + JSGAddon.VERSION + " §7( §bNexusatko §8& §bMineDragonCZ_ §7)")))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://justsgmod.eu"))));
            chat.getPlayer().sendStatusMessage(new TextComponentString("§eJSGAddon " + JSGAddon.VERSION + " §7( §bNexusatko §8& §bMineDragonCZ_ §7)"), true);
        } else if (chat.getMessage().contains("JSG Discord")) {
            ITextComponent received = chat.getComponent();
            chat.setComponent(received.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("JSG's Discord - Click me!")))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/NXp4NA8mBR"))));
            chat.getPlayer().sendStatusMessage(new TextComponentString("Click on message in chat to join our Discord!"), true);
        }
    }
}

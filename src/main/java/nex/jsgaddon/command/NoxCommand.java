package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.scheduled.ScheduledTask;
import nex.jsgaddon.scheduled.ScheduledTasksStatic;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;

import javax.annotation.Nullable;

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;


public class NoxCommand extends CommandBase {

    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }

    @Override
    public String getName() {
        return "nox";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/nox";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        if (tileEntity == null) {
            sender.sendMessage(new TextComponentString("Can't find Stargate in your radius."));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Enter an address name!"));
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        JsonStargateAddress foundAddress = findAddress(args[0].replace("-", " "), casted.getSymbolType());

        if (foundAddress == null) {
            sender.sendMessage(new TextComponentString("Invalid address name!"));
            return;
        }

        int time = 0;
        sender.sendMessage(new TextComponentString("Dialing started: " + args[0]));
        if (casted instanceof StargateUniverseBaseTile) {
            ((StargateUniverseBaseTile) casted).dialAddress(foundAddress.toImmutable(), foundAddress.size());
        } else {
            int dialed = 0;
            for (SymbolInterface symbol : foundAddress.getAddress()) {
                int maxSymbols = 8;
                DHDAbstractTile dhd = null;
                if (casted instanceof StargatePegasusBaseTile)
                    dhd = ((StargatePegasusBaseTile) casted).getLinkedDHD(casted.getWorld());
                if (casted instanceof StargateMilkyWayBaseTile)
                    dhd = ((StargateMilkyWayBaseTile) casted).getLinkedDHD(casted.getWorld());
                if (dhd != null && !dhd.hasUpgrade(DHDAbstractTile.DHDUpgradeEnum.CHEVRON_UPGRADE))
                    maxSymbols = 6;

                if (dialed < maxSymbols || symbol.origin()) {
                    dialed++;
                    time += 20;
                    ScheduledTasksStatic.add(new ScheduledTask(time, () -> {
                        if (casted.canAddSymbol(symbol)) {
                            casted.addSymbolToAddressDHD(symbol);
                        }
                    }));
                }
            }
            ScheduledTasksStatic.add(new ScheduledTask(time + 40, () -> {
                if (casted instanceof StargatePegasusBaseTile)
                    casted.addSymbolToAddressDHD(casted.getSymbolType().getBRB());
                else
                    casted.attemptOpenAndFail();
            }));
        }
    }
}

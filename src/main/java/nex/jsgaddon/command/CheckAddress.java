package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.utils.FindNearestTile;
import nex.jsgaddon.utils.GetRequiredEnergy;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.power.StargateEnergyRequired;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nullable;
import java.util.Objects;

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;

public class CheckAddress extends CommandBase {

    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }

    @Override
    public String getName() {
        return "checkaddress";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/checkaddress";
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
        StargateOpenResult status = casted.checkAddressAndEnergy(foundAddress);
        StargateEnergyRequired energy = new GetRequiredEnergy().getEnergyRequiredToDial(Objects.requireNonNull(casted.getNetwork().getStargate(casted.getStargateAddress(casted.getSymbolType()))), Objects.requireNonNull(casted.getNetwork().getStargate(foundAddress)));
        String name = String.format("Energy requirements and status for : §6" + args[0]);
        String requiredEnergy = String.format("§fRequired energy to dial §7: §e%,d §bRF", energy.energyToOpen);
        String keepEnergy = String.format("§fRequired energy to keep connection §7: §e%,d §bRF§7/§8t", energy.keepAlive);
        String info = String.format("Status of address §7: " + status);
        sender.sendMessage(new TextComponentString(name + "\n" + requiredEnergy + "\n" + keepEnergy + "\n" + info));
    }
}

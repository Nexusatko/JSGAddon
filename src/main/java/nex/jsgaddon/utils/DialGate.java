package nex.jsgaddon.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.JsonStargateAddress;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.api.controller.StargateClassicController;
import tauri.dev.jsg.stargate.EnumDialingType;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nullable;

import static nex.jsgaddon.loader.StargateAddressList.ADDRESS_MAP;

public class DialGate {
    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }


    public static void dialGate(ICommandSender sender, EntityPlayer player, Boolean noEnergy, String stargateAddress, EnumDialingType dialingType) {
        if (player == null) return;
        TileEntity tileEntity;
        EnumDialingType dialTypeParse = dialingType;

        tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);

        if (tileEntity == null) {
            Messages.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        StargateClassicBaseTile originGate = (StargateClassicBaseTile) tileEntity;
        StargateClassicController originController = StargateClassicController.getController(originGate);
        JsonStargateAddress foundAddress = findAddress(stargateAddress.replace("-", " "), originGate.getSymbolType());

        if (foundAddress == null) {
            Messages.sendErrorMess(sender,"Invalid address name!");
            return;
        }
        if (!originGate.getStargateState().idle() || originGate.getDialedAddress().size() > 0) {
            Messages.sendErrorMess(sender, "Gate is busy!");
            return;
        }
        StargatePos destinationGatePos = originGate.getNetwork().getStargate(foundAddress);
        if (destinationGatePos == null) {
            Messages.sendErrorMess(sender, "Can not find that stargate.");
            return;
        }
        StargateClassicBaseTile destinationGate = (StargateClassicBaseTile) destinationGatePos.getTileEntity();
        StargateClassicController destinationController = StargateClassicController.getController(destinationGate);
        if (destinationController.getStargateState().failing() || destinationController.getStargateState().engaged() || destinationController.getStargateState().incoming()) {
            Messages.sendErrorMess(sender, "Destination gate is busy!");
            return;
        }
        if (destinationController.getStargateState().idle() || destinationController.getStargateState().dialing()) {
            Messages.sendRunningMess(sender, new TextComponentString("Dialing started: " + stargateAddress));
            // IF DRAGON FIXES HIS DAMN CODE, DELETE THIS
            if (dialTypeParse == EnumDialingType.NOX && (destinationController.getSymbolType() == SymbolTypeEnum.UNIVERSE || originController.getSymbolType() == SymbolTypeEnum.UNIVERSE))
                dialTypeParse = EnumDialingType.FAST;
            StargateAddressDynamic targetAddress = new StargateAddressDynamic(destinationController.getStargateAddress(originGate.getSymbolType()));
            int symbolsCount = originGate.getMinimalSymbolsToDial(destinationController.getSymbolType(), destinationGatePos);
            originGate.dialAddress(targetAddress, symbolsCount - 1, noEnergy, dialTypeParse);
            if (destinationGate.getIrisState() != EnumIrisState.OPENED && destinationGate.hasIris() || destinationGate.getIrisState() != EnumIrisState.OPENING && destinationGate.hasIris()) {
                Messages.sendErrorMess(sender, "Destination gate has an active iris! Use /jsga iris or GDO to bypass it.");
            } else {
                Messages.sendSuccessMess(sender, "No active iris detected at destination! You're free to go.");
            }
        }
    }

    public static void dialGates(ICommandSender sender, String originAddress, String targetAddress, EnumDialingType dialingType, Boolean noEnergy) {
        if (originAddress == null || targetAddress == null) return;
        TileEntity originTileEntity;
        TileEntity destinationTileEntity;
        StargateClassicBaseTile originGate;
        StargateClassicBaseTile destinationGate;
        StargateClassicController originController;
        StargateClassicController destinationController;
        SymbolTypeEnum symbolType = SymbolTypeEnum.MILKYWAY;
        JsonStargateAddress jsonOriginAddress = findAddress(originAddress.replace("-", " "), symbolType);
        JsonStargateAddress jsonTargetAddress = findAddress(targetAddress.replace("-", " "), symbolType);

        StargatePos originGatePos = StargateNetwork.get(JSG.currentServer.getWorld(0)).getStargate(jsonOriginAddress);
        StargatePos destinationGatePos = StargateNetwork.get(JSG.currentServer.getWorld(0)).getStargate(jsonTargetAddress);

        if (originGatePos == null) {
            Messages.sendErrorMess(sender, "Can not find origin gate!");
            return;
        }
        if (destinationGatePos == null) {
            Messages.sendErrorMess(sender, "Can not find destination gate!");
            return;
        }
        originTileEntity = originGatePos.getTileEntity();
        destinationTileEntity = destinationGatePos.getTileEntity();
        originGate = (StargateClassicBaseTile) originTileEntity;
        destinationGate = (StargateClassicBaseTile) destinationTileEntity;
        originController = StargateClassicController.getController((StargateClassicBaseTile) originTileEntity);
        destinationController = StargateClassicController.getController((StargateClassicBaseTile) destinationTileEntity);

        if (!originController.getStargateState().idle() || originController.getDialedAddress().size() > 0) {
            Messages.sendErrorMess(sender, "Origin gate is busy!");
            return;
        }

        if (!destinationController.getStargateState().idle() || destinationController.getDialedAddress().size() > 0) {
            Messages.sendErrorMess(sender, "Destination gate is busy!");
            return;
        }
        // IF DRAGON FIXES HIS DAMN CODE, DELETE THIS
        if (dialingType == EnumDialingType.NOX && (destinationController.getSymbolType() == SymbolTypeEnum.UNIVERSE || originController.getSymbolType() == SymbolTypeEnum.UNIVERSE))
            dialingType = EnumDialingType.FAST;
        StargateAddressDynamic targetAddressDynamic = new StargateAddressDynamic(destinationController.getStargateAddress(originGate.getSymbolType()));

        int symbolsCount = originGate.getMinimalSymbolsToDial(destinationController.getSymbolType(), destinationGatePos);

        Messages.sendRunningMess(sender, new TextComponentString("Dialing from " + originAddress + " to " + targetAddress + " started."));
        originGate.dialAddress(targetAddressDynamic, symbolsCount - 1, noEnergy, dialingType);
        if (destinationGate.getIrisState() != EnumIrisState.OPENED && destinationGate.hasIris() || destinationGate.getIrisState() != EnumIrisState.OPENING && destinationGate.hasIris()) {
            Messages.sendErrorMess(sender, "Destination gate has an active iris! Use /jsga iris or GDO to bypass it.");
        } else {
            Messages.sendSuccessMess(sender, "No active iris detected at destination! You're free to go.");
        }
    }
}

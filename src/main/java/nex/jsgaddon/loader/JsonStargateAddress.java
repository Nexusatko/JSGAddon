package nex.jsgaddon.loader;

import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.util.ArrayList;

public class JsonStargateAddress extends StargateAddressDynamic {
    public JsonStargateAddress(String symbolType, ArrayList<String> symbols) {
        super(SymbolTypeEnum.valueOf(symbolType));
        address.clear();
        for (String s : symbols) {
            address.add(this.symbolType.fromEnglishName(s));
        }
    }

    public String toString() {
        return "[symbolType=" + symbolType.toString() + ", address=[" + addressToString() + "]]";
    }

    public String addressToString() {
        StringBuilder s = new StringBuilder();
        int i = 0;
        for (SymbolInterface symbol : address) {
            s.append('\'').append(symbol.getEnglishName()).append('\'');
            if (++i < address.size()) s.append(',');
        }
        return s.toString();
    }

}

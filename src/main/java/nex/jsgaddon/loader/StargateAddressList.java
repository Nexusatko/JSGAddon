package nex.jsgaddon.loader;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import nex.jsgaddon.JSGAddon;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StargateAddressList {
    public static final Map<String, Map<String, JsonStargateAddress>> ADDRESS_MAP = new HashMap<>();
    private static final Gson gson = new Gson();

    public static void load() {
        ADDRESS_MAP.clear();
        File configFile = new File("config/jsgaddon/addresslist.json");

        try {
            if (!configFile.exists()) {
                JSGAddon.logger.info(configFile.getParentFile().mkdirs());
                JSGAddon.logger.info(configFile.createNewFile());
                writeExample(configFile);
            }

            Type mapType = new TypeToken<Map<String, Map<String, ArrayList<String>>>>() {}.getType();
            Map<String, Map<String, ArrayList<String>>> addressMap = gson.fromJson(new FileReader(configFile), mapType);

            for (Map.Entry<String, Map<String, ArrayList<String>>> entry : addressMap.entrySet()) {
                String name = entry.getKey();
                Map<String, JsonStargateAddress> map = new HashMap<>();
                for (Map.Entry<String, ArrayList<String>> innerEntry : entry.getValue().entrySet()) {
                    String type = innerEntry.getKey();
                    ArrayList<String> symbols = innerEntry.getValue();
                    map.put(type, new JsonStargateAddress(type, symbols));
                }
                ADDRESS_MAP.put(name, map);
            }
            JSGAddon.info("Loaded " + ADDRESS_MAP.size() + " addresses from the file!");
        } catch (IOException e) {
            JSGAddon.error("Error while loading addresses from file!");
            JSGAddon.error(e.getMessage());
        }
    }
    public static void update() throws IOException {
        if (ADDRESS_MAP.isEmpty()) {
            writeExample(new File("config/jsgaddon/addresslist.json"));
        }
    }

    public static void reload() throws IOException {
        load();
    }
    private static void writeExample(File configFile) throws IOException {
        Map<String, Map<String, ArrayList<String>>> exampleMap = new HashMap<>();
        Map<String, ArrayList<String>> exampleAddress = new HashMap<>();
        Random rand = new Random();

        for (SymbolTypeEnum type : SymbolTypeEnum.values()) {
            ArrayList<String> symbols = new ArrayList<>();
            for (int u = 0; u < 6; u++) {
                symbols.add(type.getRandomSymbol(rand).getEnglishName());
            }
            symbols.add(type.getOrigin().getEnglishName());
            exampleAddress.put(type.toString(), symbols);
        }

        exampleMap.put("Example", exampleAddress);

        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(exampleMap, writer);
        }
    }
}

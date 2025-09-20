package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ClientModInitializer;
import net.nikdo53.tinymultiblocklib.client.ClientEvents;

public class TinyMultiBlockLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEvents.init();
    }
}

package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ClientModInitializer;
import net.nikdo53.tinymultiblocklib.client.TMBLClientEvents;

public class TinyMultiBlockLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TMBLClientEvents.init();
    }
}

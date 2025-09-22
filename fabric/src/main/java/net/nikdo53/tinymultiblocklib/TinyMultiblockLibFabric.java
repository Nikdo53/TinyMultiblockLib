package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;
import net.nikdo53.tinymultiblocklib.init.TMBLBlocks;

public class TinyMultiblockLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
           TMBLBlocks.register();
           TMBLBlockEntities.register();
       }
    }
}

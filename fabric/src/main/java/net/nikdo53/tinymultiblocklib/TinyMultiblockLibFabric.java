package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ModInitializer;
import net.nikdo53.tinymultiblocklib.init.ModBlockEntities;
import net.nikdo53.tinymultiblocklib.init.ModBlocks;

public class TinyMultiblockLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
        ModBlocks.register();
        ModBlockEntities.register();
    }
}

package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;

public class TinyMultiblockLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        if (FabricLauncherBase.getLauncher().isDevelopment())
            FabricEvents.register();
    }

}

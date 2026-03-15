package net.nikdo53.tinymultiblocklib.platform;

import net.nikdo53.tinymultiblocklib.platform.services.IEventPoster;
import net.nikdo53.tinymultiblocklib.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.platform.services.IUtils;
import org.spongepowered.asm.mixin.FabricUtil;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IEventPoster getEventPoster() {
        return FabricEventPoster.INSTANCE;
    }

    @Override
    public IUtils getUtils() {
        return FabricUtils.INSTANCE;
    }

    @Override
    public IRegistrationUtils getRegistration() {
        return FabricRegistration.INSTANCE;
    }
}

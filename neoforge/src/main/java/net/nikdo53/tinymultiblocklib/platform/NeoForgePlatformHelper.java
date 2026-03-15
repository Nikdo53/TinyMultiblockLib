package net.nikdo53.tinymultiblocklib.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.nikdo53.tinymultiblocklib.platform.services.IEventPoster;
import net.nikdo53.tinymultiblocklib.platform.services.IPlatformHelper;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.platform.services.IUtils;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public IEventPoster getEventPoster() {
        return NeoForgeEventPoster.INSTANCE;
    }

    @Override
    public IUtils getUtils() {
        return NeoForgeUtils.INSTANCE;
    }

    @Override
    public IRegistrationUtils getRegistration() {
        return NeoForgeRegistration.INSTANCE;
    }
}
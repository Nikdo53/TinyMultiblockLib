package net.nikdo53.tinymultiblocklib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;
import net.nikdo53.tinymultiblocklib.init.TMBLBlocks;
import net.nikdo53.tinymultiblocklib.init.TMBLItems;
import software.bernie.geckolib.GeckoLib;

public class TestRegistration {
    public static void register(IEventBus modEventBus) {
        TMBLBlocks.BLOCKS.register(modEventBus);
        TMBLItems.ITEMS.register(modEventBus);
        TMBLBlockEntities.BLOCK_ENTITIES.register(modEventBus);
    }
}

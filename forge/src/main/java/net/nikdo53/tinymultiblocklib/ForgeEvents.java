package net.nikdo53.tinymultiblocklib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeEvents {
    public static void register(IEventBus modEventBus) {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::testRightClickEvent);
    }

    public static void testRightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        CommonEvents.testRightClickBlock(event.getLevel(), event.getPos(), event.getEntity());
    }
}

package net.nikdo53.tinymultiblocklib.client;

import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;

import java.util.logging.Level;

public class CommonClientEvents {
    public static void onClientTick() {
        GhostRenderer.tickAll();
    }
}

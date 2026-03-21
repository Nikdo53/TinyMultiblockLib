package net.nikdo53.tinymultiblocklib;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "tinymultiblocklib";
	public static final String MOD_NAME = "Tiny MultiBlock Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier loc(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
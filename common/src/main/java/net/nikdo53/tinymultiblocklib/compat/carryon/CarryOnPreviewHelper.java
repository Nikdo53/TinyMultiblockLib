package net.nikdo53.tinymultiblocklib.compat.carryon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

public class CarryOnPreviewHelper {

    public static boolean isValidMultiblock(Player player){
        CarryOnData carry = CarryOnDataManager.getCarryData(player);
        if (carry.isCarrying(CarryOnData.CarryType.BLOCK)){
           return IMultiBlock.isMultiblock(carry.getBlock());
        };
        return false;
    }

    public static Item getMultiblockItem(Player player){
        CarryOnData carry = CarryOnDataManager.getCarryData(player);
        return carry.getBlock().getBlock().asItem();
    }
}

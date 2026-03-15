package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TinyMultiblockLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        if (FabricLauncherBase.getLauncher().isDevelopment())
            FabricEvents.register();

        CommonRegistration.init();
    }

}

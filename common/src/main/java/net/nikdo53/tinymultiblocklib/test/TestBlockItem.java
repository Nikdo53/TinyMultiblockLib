package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestBlockItem extends BlockItem {
    public TestBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("This is an example block from TinyMultiblockLib").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        tooltipComponents.add(Component.literal("Some of its functions may be disabled outside dev env").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}

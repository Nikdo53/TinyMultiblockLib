package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class TestBlockItem extends BlockItem {
    public TestBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("This is an example block from TinyMultiblockLib").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        tooltipAdder.accept(Component.literal("Some of its functions may be disabled outside dev env").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}

package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.nikdo53.nikdocolor.IColorSupplier;
import net.nikdo53.nikdocolor.NikdoColor;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostBlockRenderer;

import java.util.List;
import java.util.function.Consumer;

public class GhostBlockItem extends Item {
    public GhostBlockItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        new GhostBlockRenderer(pos, 100, Blocks.BAMBOO_BLOCK.defaultBlockState())
                .setARGB(NikdoColor.fromHex(0x88FF11FF))
                .enableTimeFade(20)
                .addToRenderList();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("This is an example item from TinyMultiblockLib").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        tooltipComponents.add(Component.literal("Some of its functions may be disabled outside dev env").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
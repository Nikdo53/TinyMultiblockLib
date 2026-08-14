package net.nikdo53.tinymultiblocklib.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixinNeo {
    
    @WrapOperation(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/resources/model/BakedModel;FFFIILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFLjava/util/List;II)V",
            ordinal = 0)
    )
    private static void renderQuadListCheckSidesWrap(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue,
                                                     List<BakedQuad> quads, int packedLight, int packedOverlay, Operation<Void> original,
                                                     @Local Direction direction
    ) {
        RenderUtils.CheckSidesContext context = RenderUtils.CHECK_SIDES_CONTEXT;
        if (context == null){
            original.call(pose, consumer, red, green, blue, quads, packedLight, packedOverlay);
            return;
        }

        if (Block.shouldRenderFace(context.state(), context.level(), context.pos(), direction, context.pos().relative(direction))) {
            original.call(pose, consumer, red, green, blue, quads, packedLight, packedOverlay);
        }
    }

}

package net.nikdo53.tinymultiblocklib.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {

     @Accessor
     VertexFormat getFormat();

     @Accessor
     VertexFormat.Mode getMode();

     @Accessor("name")
     String getName();

}

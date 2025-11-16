package me.luligabi.yet_another_industrialization.client.renderer.item

import com.mojang.blaze3d.vertex.PoseStack
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HeadedModel
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer

object IndustrialistsGogglesCurioRenderer : ICurioRenderer {

    val STACK = ItemStack(YAIItems.INDUSTRIALISTS_GOGGLES)

    val HAND_RENDERER = ItemInHandRenderer(
        Minecraft.getInstance(),
        Minecraft.getInstance().entityRenderDispatcher,
        Minecraft.getInstance().itemRenderer
    )

    override fun <T : LivingEntity, M : EntityModel<T>> render(
        stack: ItemStack,
        ctx: SlotContext,
        ps: PoseStack,
        renderLayerParent: RenderLayerParent<T, M>,
        renderTypeBuffer: MultiBufferSource,
        light: Int,
        limbSwing: Float, limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float, headPitch: Float
    ) {
        ps.pushPose()
        (renderLayerParent.model as HeadedModel).head.translateAndRotate(ps)
        CustomHeadLayer.translateToHead(ps, false)

        HAND_RENDERER.renderItem(
            ctx.entity,
            STACK,
            ItemDisplayContext.HEAD,
            false,
            ps,
            renderTypeBuffer,
            light
        )
        ps.popPose()
    }
}
package me.luligabi.yet_another_industrialization.client.renderer

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class ArboreousGreenhouseBER(ctx: BlockEntityRendererProvider.Context) : MultiblockMachineBER(ctx) {

    override fun render(be: MultiblockMachineBlockEntity, tickDelta: Float, matrices: PoseStack, vcp: MultiBufferSource, light: Int, overlay: Int) {
        super.render(be, tickDelta, matrices, vcp, light, overlay)

        val recipe = ((be as ArboreousGreenhouseBlockEntity).crafterComponent as CrafterComponentAccessor).activeRecipe
    }

}
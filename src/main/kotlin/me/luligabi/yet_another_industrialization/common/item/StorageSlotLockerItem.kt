package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.MIComponents
import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.util.Simulation
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.misc.YAIDataComponents
import me.luligabi.yet_another_industrialization.common.misc.YAISounds
import me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData
import me.luligabi.yet_another_industrialization.common.util.*
import me.luligabi.yet_another_industrialization.mixin.MIStorageAccessor
import me.luligabi.yet_another_industrialization.mixin.UseOnContextAccessor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.HitResult
import java.util.*


class StorageSlotLockerItem(properties: Properties) : Item(
    properties.stacksTo(1)
        .component(YAIDataComponents.SLOT_LOCKER_DATA.get(), SlotLockerData.EMPTY)
        .component(MIComponents.ENERGY, 0L)
), YAIDraggable {


    override fun useOn(ctx: UseOnContext): InteractionResult {
        if (ctx.level.isClientSide) return InteractionResult.FAIL
        if ((ctx as UseOnContextAccessor).hitResult.type != HitResult.Type.BLOCK) return InteractionResult.PASS

        val data = ctx.itemInHand.get(YAIDataComponents.SLOT_LOCKER_DATA) ?: return InteractionResult.PASS

        val machine = ctx.level.getBlockEntity(ctx.clickedPos) as? MachineBlockEntity ?: return InteractionResult.PASS

        val inventory = getInventory(machine) ?: return InteractionResult.PASS

        val mode = data.mode
        val modifiedItem = when (mode.lockItem) {
            true -> if (!data.itemVariant.isBlank) {
                toggleItemLocks(inventory, true, data.itemVariant.item)
            } else false
            false -> toggleItemLocks(inventory, false, data.itemVariant.item)
            null -> false
        }
        val modifiedFluid = when (mode.lockFluid) {
            true -> if (!data.fluidVariant.isBlank) {
                toggleFluidLocks(inventory, true, data.fluidVariant.fluid)
            } else false
            false -> toggleFluidLocks(inventory, false, data.fluidVariant.fluid)
            null -> false
        }

        val success = modifiedItem || modifiedFluid
        if (success) {
            ctx.level.playSound(
                null, ctx.clickedPos,
                YAISounds.STORAGE_SLOT_LOCKER_LOCK.get(),
                SoundSource.PLAYERS, 1f, 1f
            )
        }
        return if (success) InteractionResult.sidedSuccess(ctx.level.isClientSide) else InteractionResult.FAIL
    }

    @Suppress("UNCHECKED_CAST")
    private fun toggleItemLocks(inventory: MIInventory, enable: Boolean, item: Item): Boolean {
        var modifiedAny = false
        ((inventory.itemStorage as MIStorageAccessor).stacks as List<ConfigurableItemStack>).forEach {
            if (enable) {
                if (!it.variant.isBlank || it.lockedInstance != null) return@forEach
                it.playerLock(item, Simulation.ACT)
                modifiedAny = true
            } else {
                if (it.lockedInstance != item) return@forEach
                it.togglePlayerLock()
                modifiedAny = true
            }
        }
        return modifiedAny
    }

    @Suppress("UNCHECKED_CAST")
    private fun toggleFluidLocks(inventory: MIInventory, enable: Boolean, fluid: Fluid): Boolean {
        var modifiedAny = false
        ((inventory.fluidStorage as MIStorageAccessor).stacks as List<ConfigurableFluidStack>).forEach {
            if (enable) {
                if (!it.variant.isBlank || it.lockedInstance != null) return@forEach
                it.playerLock(fluid, Simulation.ACT)
                modifiedAny = true
            } else {
                if (it.lockedInstance != fluid) return@forEach
                it.togglePlayerLock()
                modifiedAny = true
            }
        }
        return modifiedAny
    }

    private fun getInventory(machine: MachineBlockEntity): MIInventory? {
        if (machine.inventory == MIInventory.EMPTY) return null
        return machine.inventory
    }

    override fun dragItem(
        stack: ItemStack,
        itemKey: ItemVariant,
        simulation: Boolean
    ): Boolean {
        if (simulation) return true

        val data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA)?.copy(itemVariant = itemKey) ?: return false
        stack.set(YAIDataComponents.SLOT_LOCKER_DATA, data)
        return true
    }

    override fun dragFluid(
        stack: ItemStack,
        fluidKey: FluidVariant,
        simulation: Boolean
    ): Boolean {
        if (simulation) return true

        val data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA)?.copy(fluidVariant = fluidKey) ?: return false
        stack.set(YAIDataComponents.SLOT_LOCKER_DATA, data)
        return true
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        val data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA) ?: return Optional.empty<TooltipComponent>()
        return Optional.of(TooltipData(data.itemVariant, data.fluidVariant, data.mode))
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        stack.get(YAIDataComponents.SLOT_LOCKER_DATA)?.let {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(
                    YAIText.SLOT_LOCKER_TOOLTIP_1
                        .arg(itemTooltipComponent(it))
                        .arg(fluidTooltipComponent(it))
                )
            }

            tooltipComponents.add(
                YAIText.SLOT_LOCKER_MODE_PREFIX.text(
                    it.mode.text.text().applyStyle(TextHelper.NUMBER_TEXT)
                ).applyStyle(TextHelper.GRAY_TEXT)
            )
        }
    }

    private fun itemTooltipComponent(data: SlotLockerData): Component {
        val variant = data.itemVariant
        if (variant.isBlank) return YAIText.NONE.text().applyStyle(TextHelper.GRAY_TEXT)

        return variant.item.getName(variant.toStack()).copy().applyStyle(ITEM_STYLE)
    }

    private fun fluidTooltipComponent(data: SlotLockerData): Component {
        val variant = data.fluidVariant
        if (variant.isBlank) return YAIText.NONE.text().applyStyle(TextHelper.GRAY_TEXT)

        return variant.toStack(1).hoverName.copy().applyStyle(FLUID_STYLE)
    }

    data class TooltipData(
        val itemVariant: ItemVariant,
        val fluidVariant: FluidVariant,
        val mode: SlotLockerData.Mode
    ): TooltipComponent
}
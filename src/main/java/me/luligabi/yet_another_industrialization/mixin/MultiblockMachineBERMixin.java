package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER;
import me.luligabi.yet_another_industrialization.common.item.IndustrialistsGogglesItem;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import me.luligabi.yet_another_industrialization.common.misc.proxy.YAIModSlotProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiblockMachineBER.class, remap = false)
public abstract class MultiblockMachineBERMixin {

    @Inject(method = "isHoldingWrench", at = @At("TAIL"), cancellable = true)
    private static void yet_another_industrialization_isHoldingWrench(CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : YAIModSlotProxy.Companion.getHeadItems(Minecraft.getInstance().player, stack -> true)) {
            if (stack.getItem() instanceof IndustrialistsGogglesItem &&
                    stack.getOrDefault(YAIDataComponents.INSTANCE.getGOGGLES_ENABLED(), true)) {
                cir.setReturnValue(true);
            }
        }
    }

}
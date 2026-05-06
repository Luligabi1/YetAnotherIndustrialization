package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import me.luligabi.yet_another_industrialization.common.item.IndustrialistsGogglesItem;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import me.luligabi.yet_another_industrialization.common.misc.proxy.YAIModSlotProxy;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiblockMachineBER.class, remap = false)
public abstract class MultiblockMachineBERMixin {

    @Unique
    private static Boolean yet_another_industrialization$wrenchCache = null;

    @Unique
    private static int yet_another_industrialization$wrenchCacheCooldown = 0;

    @Inject(method = "isHoldingWrench", at = @At("TAIL"), cancellable = true)
    private static void yet_another_industrialization_isHoldingWrench(CallbackInfoReturnable<Boolean> cir) {
        if (yet_another_industrialization$wrenchCache != null) {
            if (--yet_another_industrialization$wrenchCacheCooldown > 0) {
                if (yet_another_industrialization$wrenchCache) cir.setReturnValue(true);
                return;
            }
            yet_another_industrialization$wrenchCache = null;
        }

        boolean result = YAIModSlotProxy.Companion.hasHeadItem(
            Minecraft.getInstance().player,
            stack -> stack.getItem() instanceof IndustrialistsGogglesItem && stack.getOrDefault(YAIDataComponents.INSTANCE.getGOGGLES_ENABLED(), true)
        );

        yet_another_industrialization$wrenchCache = result;
        yet_another_industrialization$wrenchCacheCooldown = 25;

        if (result) cir.setReturnValue(true);
    }

}
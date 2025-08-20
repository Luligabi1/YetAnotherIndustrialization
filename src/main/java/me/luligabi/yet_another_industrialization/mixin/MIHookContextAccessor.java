package me.luligabi.yet_another_industrialization.mixin;

import net.swedz.tesseract.neoforge.compat.mi.hook.MIHook;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.MIHookContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MIHookContext.class, remap = false)
public interface MIHookContextAccessor {

    @Accessor("hook")
    MIHook getHook();

}

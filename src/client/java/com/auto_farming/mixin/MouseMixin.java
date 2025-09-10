package com.auto_farming.mixin;

import com.auto_farming.actionwrapper.MouseLocker;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "updateMouse(D)V", at = @At("HEAD"), cancellable = true)
    private void freezeLook(double timeDelta, CallbackInfo ci) {
        if (MouseLocker.isMouseLocked()) {
            ci.cancel();
        }
    }
}

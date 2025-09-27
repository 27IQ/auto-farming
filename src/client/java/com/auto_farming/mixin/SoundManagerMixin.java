package com.auto_farming.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.auto_farming.sounds.AutoSoundMuter;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void onPlay(SoundInstance soundInstance, CallbackInfo ci) {
        if (!AutoSoundMuter.isSoundMuted())
            return;

        if (soundInstance.getId() != null
                && !AutoSoundMuter.IDENTIFIER_SET.contains(soundInstance.getId().toString())) {
            ci.cancel();
        }
    }
}

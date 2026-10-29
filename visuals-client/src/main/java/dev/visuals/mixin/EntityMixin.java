package dev.visuals.mixin;

import dev.visuals.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void visuals$esp(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        // тільки на клієнтському (render) потоці, щоб не чіпати сервер у singleplayer
        if (mc == null || mc.player == null || !mc.isOnThread()) return;
        Entity self = (Entity) (Object) this;
        if (self == mc.player) return;

        if ((ModuleManager.PLAYER_ESP != null && ModuleManager.PLAYER_ESP.isEnabled() && self instanceof PlayerEntity)
         || (ModuleManager.MOB_ESP != null && ModuleManager.MOB_ESP.isEnabled() && self instanceof MobEntity)
         || (ModuleManager.ITEM_ESP != null && ModuleManager.ITEM_ESP.isEnabled() && self instanceof ItemEntity)) {
            cir.setReturnValue(true);
        }
    }
}

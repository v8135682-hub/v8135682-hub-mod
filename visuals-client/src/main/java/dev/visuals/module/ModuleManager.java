package dev.visuals.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static Module FULLBRIGHT, PLAYER_ESP, MOB_ESP, ITEM_ESP;
    public static Module WATERMARK, COORDS, FPS, ARRAYLIST;

    public static void init() {
        FULLBRIGHT = add(new Module("Fullbright", Category.RENDER) {
            @Override public void onTick(MinecraftClient mc) {
                if (mc.player != null && !mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    mc.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 100000, 0, false, false, false));
                }
            }
            @Override public void onDisable() {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        });
        PLAYER_ESP = add(new Module("Player ESP", Category.RENDER) {});
        MOB_ESP    = add(new Module("Mob ESP", Category.RENDER) {});
        ITEM_ESP   = add(new Module("Item ESP", Category.RENDER) {});

        WATERMARK = add(new Module("Watermark", Category.HUD) {});
        COORDS    = add(new Module("Coordinates", Category.HUD) {});
        FPS       = add(new Module("FPS", Category.HUD) {});
        ARRAYLIST = add(new Module("ArrayList", Category.HUD) {});

        WATERMARK.setEnabled(true);
        ARRAYLIST.setEnabled(true);
    }

    private static Module add(Module m) { MODULES.add(m); return m; }
    public static List<Module> all() { return MODULES; }

    public static List<Module> of(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category == c) out.add(m);
        return out;
    }
}

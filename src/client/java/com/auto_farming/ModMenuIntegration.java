
package com.auto_farming;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ModConfig.build(parent);
        // Or alternatively, use AutoConfig's default screen:
        // return parent -> AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
}
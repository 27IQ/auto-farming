package com.auto_farming.config;

import com.auto_farming.farmprofiles.Profile;

public class SaveDataLoader {
    
    public static ModData load(){
        ModData data=new ModData();
        data.setCurrentProfile(Profile.NETHERWART);
        data.setForceAttentiveMood(false);
        data.setShowPauseMessage(true);

        return data;
    }

    public static void save(ModData data){

    }
}

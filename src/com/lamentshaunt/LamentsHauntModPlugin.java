package com.lamentshaunt;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.lamentshaunt.world.LamentsHauntGen;

public class LamentsHauntModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        // Output a log message when generating the sector
        Global.getLogger(this.getClass()).info("Lament's Haunt: Initializing custom star system generation...");
        
        // Run the star system generator
        new LamentsHauntGen().generate(Global.getSector());
    }
}

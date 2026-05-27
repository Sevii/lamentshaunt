package com.lamentshaunt;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.lamentshaunt.world.LamentsHauntGen;
import com.lamentshaunt.world.LamentsHauntColonizationFixer;

public class LamentsHauntModPlugin extends BaseModPlugin {

    @Override
    public void onNewGameAfterEconomyLoad() {
        // Generate here (not onNewGame): by this point both procgen and the economy
        // exist, so addMarket() registers our markets into the economy's master list.
        Global.getLogger(this.getClass()).info("Lament's Haunt: Initializing custom star system generation...");

        // Run the star system generator
        new LamentsHauntGen().generate(Global.getSector());
    }

    @Override
    public void onGameLoad(boolean newGame) {
        // Keep a colonization listener installed every load (transient, never saved) so
        // that colonizing one of our worlds forces its market into the economy master list
        // and onto the Colonies intel tab. Guarded so it is registered at most once.
        ListenerManagerAPI lm = Global.getSector().getListenerManager();
        if (!lm.hasListenerOfClass(LamentsHauntColonizationFixer.class)) {
            lm.addListener(new LamentsHauntColonizationFixer(), false);
        }
    }
}

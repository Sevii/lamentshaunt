package com.lamentshaunt.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;

/**
 * When the player colonizes one of our worlds, guarantee the resulting colony shows up
 * in the Intel -> Colonies list.
 *
 * That list comes from Misc.getPlayerMarkets(), which iterates
 * Global.getSector().getEconomy().getMarketsCopy() (the economy "master list") and keeps
 * markets that are player-owned. A colony that is manageable in-system but absent from the
 * list is one whose market is not in that master list, or is still flagged as a
 * condition-only / hidden market. We fix both here, right after colonization.
 */
public class LamentsHauntColonizationFixer implements PlayerColonizationListener {

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        if (planet == null || planet.getId() == null) return;
        // Only touch the planets this mod created.
        if (!planet.getId().startsWith("lamentshaunt_")) return;

        MarketAPI market = planet.getMarket();
        if (market == null) return;

        // A real, listable colony - not a survey-only condition market, not hidden.
        market.setPlanetConditionMarketOnly(false);
        market.setHidden(false);

        // Ensure it is in the economy's master list that the Colonies tab reads.
        if (!Global.getSector().getEconomy().getMarketsCopy().contains(market)) {
            Global.getSector().getEconomy().addMarket(market, false);
        }
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI market) {
        // No action needed on abandonment.
    }
}

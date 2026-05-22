package com.lamentshaunt.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.awt.Color;

public class LamentsHauntGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        // Create the star system
        StarSystemAPI system = sector.createStarSystem("Lament's Star");
        
        // Position the star system in hyperspace (away from core worlds)
        system.getLocation().set(-16300, -16000);
        
        // Set the background texture (fallback to standard Starsector background)
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
        
        // Tint the system light - pale red/orange for a red dwarf star
        system.setLightColor(new Color(255, 185, 185));
        
        // Add a star - class star_red_dwarf
        PlanetAPI star = system.initStar(
            "lamentshaunt_star", // unique internal id
            "star_red_dwarf",    // type (refers to star types in star_types.json)
            250f,                // radius (red dwarf star is smaller)
            400f                 // corona width
        );
        star.setName("Lament");
        
        // Add the barren planet closely orbiting the star
        PlanetAPI barrenPlanet = system.addPlanet(
            "lamentshaunt_barren",      // unique internal id
            star,                       // focus of orbit
            "Cinder",                   // display name
            "barren",                   // type
            220f,                       // starting orbit angle
            85f,                        // radius
            1200f,                      // orbit radius in pixels (very close to star)
            80f                         // orbital period in game days
        );
        barrenPlanet.setCustomDescriptionId("lamentshaunt_barren_desc");
        
        // Create condition-only market for the barren planet
        MarketAPI barrenMarket = Global.getFactory().createMarket(
            "lamentshaunt_barren_market",
            barrenPlanet.getName(),
            0
        );
        barrenMarket.setPrimaryEntity(barrenPlanet);
        barrenMarket.setPlanetConditionMarketOnly(true);
        barrenMarket.addCondition("extreme_heat");
        barrenMarket.addCondition("no_atmosphere");
        barrenMarket.addCondition("ore_rich");
        barrenMarket.addCondition("rare_ore_ultrarich");
        barrenMarket.setFactionId("neutral");
        barrenPlanet.setMarket(barrenMarket);
        Global.getSector().getEconomy().addMarket(barrenMarket, true);
        
        // Add the fluorescent gas giant orbiting the star
        PlanetAPI fluorescentPlanet = system.addPlanet(
            "lamentshaunt_fluorescent", // unique internal id
            star,                       // focus of orbit
            "Glimmer",                  // display name
            "US_fluorescent",             // type
            45f,                        // starting orbit angle
            140f,                       // radius
            4500f,                      // orbit radius in pixels
            400f                        // orbital period in game days
        );
        fluorescentPlanet.setCustomDescriptionId("lamentshaunt_fluorescent_desc");
        
        // Create condition-only market for the fluorescent planet to hold its Unknown Skies condition
        MarketAPI fluorescentMarket = Global.getFactory().createMarket(
            "lamentshaunt_fluorescent_market",
            fluorescentPlanet.getName(),
            0
        );
        fluorescentMarket.setPrimaryEntity(fluorescentPlanet);
        fluorescentMarket.setPlanetConditionMarketOnly(true);
        fluorescentMarket.addCondition("US_fluorescent");
        fluorescentMarket.addCondition("US_floating");
        fluorescentMarket.addCondition("volatiles_plentiful");
        fluorescentMarket.addCondition("high_gravity");
        fluorescentMarket.setFactionId("neutral");
        fluorescentPlanet.setMarket(fluorescentMarket);
        Global.getSector().getEconomy().addMarket(fluorescentMarket, true);
        
        // Add a beautiful fluorescent cyan ring band around Glimmer
        system.addRingBand(
            fluorescentPlanet,
            "misc",
            "rings_dust0",
            64f,
            0,
            new Color(150, 240, 255, 150),
            64f,
            300f,
            60f
        );
        
        // Add the small jungle world moon orbiting the fluorescent gas giant
        PlanetAPI jungleMoon = system.addPlanet(
            "lamentshaunt_jungle", // unique internal id
            fluorescentPlanet,     // focus of orbit (orbiting the fluorescent planet)
            "Haunt",               // display name
            "jungle",              // type
            135f,                  // starting orbit angle
            50f,                   // radius (small size)
            600f,                  // orbit radius in pixels from focus
            30f                    // orbital period in game days
        );
        jungleMoon.setCustomDescriptionId("lamentshaunt_jungle_desc");
        
        // Create condition-only market for the jungle moon
        MarketAPI jungleMarket = Global.getFactory().createMarket(
            "lamentshaunt_jungle_market",
            jungleMoon.getName(),
            0
        );
        jungleMarket.setPrimaryEntity(jungleMoon);
        jungleMarket.setPlanetConditionMarketOnly(true);
        jungleMarket.addCondition("habitable");
        jungleMarket.addCondition("farmland_rich");
        jungleMarket.addCondition("organics_abundant");
        jungleMarket.setFactionId("neutral");
        jungleMoon.setMarket(jungleMarket);
        Global.getSector().getEconomy().addMarket(jungleMarket, true);
        
        // Add 3 stable hyperspace areas in the system
        SectorEntityToken stable1 = system.addCustomEntity(
            "lamentshaunt_stable1",
            "Stable Location",
            "stable_location",
            "neutral"
        );
        stable1.setCircularOrbit(star, 15f, 2000f, 180f);
        
        // Add 3 stable locations in the system
        SectorEntityToken stable2 = system.addCustomEntity(
            "lamentshaunt_stable2",
            "Stable Location",
            "stable_location",
            "neutral"
        );
        stable2.setCircularOrbit(star, 135f, 3200f, 240f);
        
        SectorEntityToken stable3 = system.addCustomEntity(
            "lamentshaunt_stable3",
            "Stable Location",
            "stable_location",
            "neutral"
        );
        stable3.setCircularOrbit(star, 255f, 6000f, 480f);
        
        // Autogenerate jump points for the system to make it accessible from hyperspace
        system.autogenerateHyperspaceJumpPoints(true, true);
    }
}

package com.lamentshaunt.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent.SkillPickPreference;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import java.awt.Color;

public class LamentsHauntGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        // Create the star system
        StarSystemAPI system = sector.createStarSystem("Lament's Star");
        system.setProcgen(true);
        
        // Position the star system in hyperspace (away from core worlds)
        system.getLocation().set(-19560, -19200);
        
        // Set the background texture (fallback to standard Starsector background)
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
        
        // Tint the system light - warm, deep red/orange for a red dwarf star
        system.setLightColor(new Color(255, 150, 120));
        
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
        barrenMarket.setSurveyLevel(MarketAPI.SurveyLevel.NONE);
        barrenMarket.addCondition("very_hot");
        barrenMarket.addCondition("no_atmosphere");
        barrenMarket.addCondition("ore_rich");
        barrenMarket.addCondition("rare_ore_ultrarich");
        barrenMarket.setFactionId("neutral");
        barrenPlanet.setMarket(barrenMarket);
        Global.getSector().getEconomy().addMarket(barrenMarket, true);

        // A big, warm dust ring encircling the red dwarf, filling the gap before Glimmer
        system.addRingBand(
            star,
            "misc",
            "rings_dust0",
            512f,                          // band texture scale
            0,                             // band index
            new Color(255, 170, 140, 180), // warm tone, lit by the red dwarf
            512f,                          // ring width
            2200f,                         // orbit radius from the star
            260f                           // orbital period in days
        );

        // A very sparse scattering of asteroids tumbling through the big ring
        system.addAsteroidBelt(
            star,
            14,    // very sparse - reads as a faint debris ring, not a field
            2200f, // orbit radius (sits on the big ring)
            360f,  // belt width
            240f,  // min orbital period
            300f   // max orbital period
        );

        // Add the fluorescent gas giant orbiting the star
        PlanetAPI fluorescentPlanet = system.addPlanet(
            "lamentshaunt_fluorescent", // unique internal id
            star,                       // focus of orbit
            "Glimmer",                  // display name
            "US_fluorescent",             // type
            45f,                        // starting orbit angle
            168f,                       // radius (increased by 20%)
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
        fluorescentMarket.setSurveyLevel(MarketAPI.SurveyLevel.NONE);
        fluorescentMarket.addCondition("US_fluorescent");
        fluorescentMarket.addCondition("US_floating");
        fluorescentMarket.addCondition("volatiles_plentiful");
        fluorescentMarket.addCondition("high_gravity");
        fluorescentMarket.setFactionId("neutral");
        fluorescentPlanet.setMarket(fluorescentMarket);
        Global.getSector().getEconomy().addMarket(fluorescentMarket, true);

        // The signature glow: a cyan-tuned magnetic field (aurora) shrouding Glimmer
        SectorEntityToken glimmerField = system.addTerrain(
            Terrain.MAGNETIC_FIELD,
            new MagneticFieldTerrainPlugin.MagneticFieldParams(
                fluorescentPlanet.getRadius() + 200f, // band width
                fluorescentPlanet.getRadius() + 100f, // middle radius
                fluorescentPlanet,                    // related entity
                fluorescentPlanet.getRadius() + 50f,  // inner radius
                fluorescentPlanet.getRadius() + 350f, // outer radius
                new Color(20, 60, 90, 40),            // base tint
                0.25f,                                 // aurora frequency
                new Color(80, 230, 255, 255),          // aurora palette...
                new Color(140, 240, 255, 255),
                new Color(90, 200, 235, 255),
                new Color(120, 255, 245, 255),
                new Color(60, 210, 255, 255)
            )
        );
        glimmerField.setCircularOrbit(fluorescentPlanet, 0f, 0f, 100f);

        // Layered fluorescent ring bands around Glimmer for Saturn-like depth
        system.addRingBand(
            fluorescentPlanet,
            "misc",
            "rings_dust0",
            256f,
            0,
            new Color(150, 240, 255, 140), // inner, faint
            256f,
            380f,
            55f
        );
        system.addRingBand(
            fluorescentPlanet,
            "misc",
            "rings_ice0",
            256f,
            0,
            new Color(120, 235, 255, 230), // bright main band
            256f,
            470f,
            65f
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
        jungleMarket.setSurveyLevel(MarketAPI.SurveyLevel.NONE);
        jungleMarket.addCondition("habitable");
        jungleMarket.addCondition("farmland_rich");
        jungleMarket.addCondition("organics_abundant");
        jungleMarket.setFactionId("neutral");
        jungleMoon.setMarket(jungleMarket);
        Global.getSector().getEconomy().addMarket(jungleMarket, true);

        // A salvageable derelict drifting near Haunt as set dressing
        SectorEntityToken derelictProbe = system.addCustomEntity(
            "lamentshaunt_probe",
            "Derelict Probe",
            "derelict_survey_ship",
            "neutral"
        );
        derelictProbe.setCircularOrbit(jungleMoon, 200f, 420f, 35f);

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
        
        // Drape a systemwide nebula over everything for mood and depth
        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);

        // Autogenerate jump points for the system to make it accessible from hyperspace
        system.autogenerateHyperspaceJumpPoints(true, true);
        
        // Spawn the boss fleet orbiting the jungle moon Haunt
        CampaignFleetAPI bossFleet = FleetFactoryV3.createEmptyFleet(
            Factions.PIRATES,
            FleetTypes.PATROL_LARGE,
            null
        );
        
        bossFleet.setName("Lament's fleet remenants");
        bossFleet.setNoFactionInName(true);
        bossFleet.getFleetData().addFleetMember("paragon_Elite");
        bossFleet.getFleetData().addFleetMember("tempest_Attack");
        
        // Sync fleet composition
        bossFleet.getFleetData().setSyncNeeded();
        bossFleet.forceSync();
        
        // Flagship setup
        FleetMemberAPI flagship = bossFleet.getFleetData().getMembersListCopy().get(0);
        bossFleet.getFleetData().setFlagship(flagship);
        
        // Create level 5 officers with aggressive personalities
        PersonAPI captain1 = OfficerManagerEvent.createOfficer(
            Global.getSector().getFaction(Factions.PIRATES),
            5
        );
        captain1.setPersonality(Personalities.AGGRESSIVE);
        flagship.setCaptain(captain1);
        bossFleet.setCommander(captain1);
        
        FleetMemberAPI tempest = bossFleet.getFleetData().getMembersListCopy().get(1);
        PersonAPI captain2 = OfficerManagerEvent.createOfficer(
            Global.getSector().getFaction(Factions.PIRATES),
            5
        );
        captain2.setPersonality(Personalities.AGGRESSIVE);
        tempest.setCaptain(captain2);
        
        // Make the fleet extremely aggressive and target the player
        MemoryAPI mem = bossFleet.getMemoryWithoutUpdate();
        mem.set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        mem.set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        mem.set(MemFlags.MEMORY_KEY_PURSUE_PLAYER, true);
        mem.set(MemFlags.FLEET_DO_NOT_IGNORE_PLAYER, true);
        mem.set(MemFlags.MEMORY_KEY_LOW_REP_IMPACT, true);
        mem.set(MemFlags.MEMORY_KEY_NO_REP_IMPACT, true);
        
        // Visibility and transponder
        bossFleet.setTransponderOn(false);
        bossFleet.getDetectedRangeMod().modifyFlat("boss", 5000f);
        
        // Add to the star system orbiting the jungle moon Haunt
        system.addEntity(bossFleet);
        bossFleet.setLocation(jungleMoon.getLocation().x, jungleMoon.getLocation().y);
        bossFleet.addAssignment(
            FleetAssignment.ORBIT_AGGRESSIVE,
            jungleMoon,
            1000000f
        );
    }
}

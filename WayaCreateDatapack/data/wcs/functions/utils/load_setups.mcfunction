scoreboard objectives add wcs.temp dummy
scoreboard objectives add wcs.mine_wood_cd dummy
scoreboard objectives add wcs.use_item minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add wcs.progressStage dummy "WCS Player Progression"

# Chat Command Triggers
scoreboard objectives add wcs.cmd_help trigger "WCS: Show Help"
scoreboard objectives add wcs.cmd_giveDia trigger "WCS: Give Diamonds"
scoreboard objectives add wcs.cmd_timeStop trigger "WCS: Stop Time"
scoreboard objectives add wcs.cmd_timeResume trigger "WCS: Resume Time"
scoreboard objectives add wcs.cmd_wcModeOn trigger "WCS: WayaCreate Mode ON"
scoreboard objectives add wcs.cmd_wcModeOff trigger "WCS: WayaCreate Mode OFF"

# wcs_allies team (temporary grouping for become_ally_to_monsters)
# Clear the wcs_allies team just in case of reload, as it's meant to be temporary.
team remove wcs_allies
team add wcs_allies {"text":"WCS Allies"}
team modify wcs_allies friendlyFire false
team modify wcs_allies color green

# wcs_army team (for recruited mobs)
team add wcs_army {"text":"WCS Army"}
team modify wcs_army friendlyFire false
team modify wcs_army color blue

# Add other setup commands here as needed
tellraw @a {"text":"WayaCreateDatapack loaded! WCS utilities initialized. (v6 - Guidance System)","color":"gold"}

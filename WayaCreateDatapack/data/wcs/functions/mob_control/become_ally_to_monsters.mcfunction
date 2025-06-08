# This is a simple version making nearby monsters temporarily passive by putting them on a team.
# Ensure the team exists and is configured (no friendly fire, etc.)
team add wcs_allies {"text":"WCS Allies"}
team modify wcs_allies friendlyFire false
team modify wcs_allies color green
execute as @e[type=!minecraft:player,distance=..16,team=!wcs_allies] run team join wcs_allies @s
execute as @e[type=!minecraft:player,distance=..16,team=wcs_allies] run data merge entity @s {NoAI:0b, Health:20f}
# Add a temporary effect to them so we can remove them from the team later
effect give @e[type=!minecraft:player,distance=..16,team=wcs_allies] minecraft:glowing 30 0 true
tellraw @s {"text":"WayaCreate says: Nearby creatures seem less hostile for a moment...","color":"green"}
# Schedule a function to remove them from the team after 30 seconds
schedule function wcs:mob_control/clear_allies_team 30s

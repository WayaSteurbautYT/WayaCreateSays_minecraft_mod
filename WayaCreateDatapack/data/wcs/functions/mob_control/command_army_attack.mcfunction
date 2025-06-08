# This is complex. It requires the player to target an entity, then have army members target it.
# For now, placeholder: makes army members aggressive (removes NoAI) and they pick their own targets.
execute as @e[team=wcs_army,type=!minecraft:player] run data merge entity @s {NoAI:0b}
tellraw @s {"text":"WayaCreate says: Army, attack! (They will choose their own targets for now)","color":"red"}

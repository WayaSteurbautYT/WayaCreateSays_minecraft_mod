# This needs a way to target a mob. For now, assume the mob is within 2 blocks directly in front.
# More advanced: raycast to find target.
# Ensure the 'wcs_army' team exists (add to load_setups.mcfunction)
execute as @e[type=!minecraft:player,distance=..2,limit=1,sort=nearest,team=!wcs_army] at @s run team join wcs_army @s
execute as @e[type=!minecraft:player,distance=..2,limit=1,sort=nearest,team=wcs_army] at @s run tellraw @p {"text":"WayaCreate says: You've recruited a new member to your army!","color":"blue"}
execute as @e[type=!minecraft:player,distance=..2,limit=1,sort=nearest,team=!wcs_army] at @s run tellraw @p {"text":"WayaCreate says: Couldn't recruit. Maybe it's already in your army or too far?","color":"yellow"}

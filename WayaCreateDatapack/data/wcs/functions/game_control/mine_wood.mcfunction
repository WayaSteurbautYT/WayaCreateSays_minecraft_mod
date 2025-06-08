tellraw @s {"text":"WayaCreate says: Let's get chopping! (This feature is basic and will try to mine one log in front of you)","color":"green"}
# This is a simplified version. It targets a single log block directly in front of the player at eye level.
# A full version would require raycasting or checking a larger area and different wood types.
execute at @s anchored eyes positioned ^ ^ ^1 execute if block ~ ~ ~ minecraft:oak_log run setblock ~ ~ ~ minecraft:air destroy
execute at @s anchored eyes positioned ^ ^ ^1 execute if block ~ ~ ~ minecraft:spruce_log run setblock ~ ~ ~ minecraft:air destroy
execute at @s anchored eyes positioned ^ ^ ^1 execute if block ~ ~ ~ minecraft:birch_log run setblock ~ ~ ~ minecraft:air destroy
# Add other log types as needed (dark_oak, acacia, jungle, mangrove, cherry, crimson_stem, warped_stem)
# Potentially give item based on what was destroyed, or rely on 'destroy' dropping it.
# Add a small cooldown to prevent spam/lag
scoreboard players add @s wcs.mine_wood_cd 100
schedule function wcs:utils/reset_mine_wood_cd 5s

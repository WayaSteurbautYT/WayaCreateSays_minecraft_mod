# Makes members of wcs_army follow the player who issued the command.
# This requires storing the commander's identity, perhaps by tagging the player.
tag @s add wcs_army_commander
# In a tick function, mobs in wcs_army would pathfind to @a[tag=wcs_army_commander,limit=1]
# For now, a simpler teleport to player who used the item/command.
# Also ensure NoAI is off so they can move (it's set by 'stay' command)
execute as @e[team=wcs_army,type=!minecraft:player] run data merge entity @s {NoAI:0b}
execute as @e[team=wcs_army,type=!minecraft:player] run tp @s @p
tellraw @s {"text":"WayaCreate says: Army, follow me!","color":"blue"}

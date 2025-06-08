# Simplified: For now, gives the player an ender pearl if they are looking at an enderman.
# A full version would use custom loot tables or more specific targeting.
execute as @s at @s if entity @e[type=minecraft:enderman,distance=..4,limit=1] run give @s minecraft:ender_pearl 1
execute as @s at @s if entity @e[type=minecraft:enderman,distance=..4,limit=1] run tellraw @s {"text":"WayaCreate says: You managed to snag an ender pearl from the Enderman!","color":"dark_purple"}
execute as @s at @s unless entity @e[type=minecraft:enderman,distance=..4,limit=1] run tellraw @s {"text":"WayaCreate says: Look closely at an Enderman to use this!","color":"gray"}

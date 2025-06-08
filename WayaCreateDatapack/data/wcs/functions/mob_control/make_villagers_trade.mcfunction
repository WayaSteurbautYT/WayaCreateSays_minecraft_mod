# Simplified: Refreshes trades for villagers near the player.
# True "force trade" is complex. This will just reset their trades if possible.
execute as @e[type=minecraft:villager,distance=..8] run data merge entity @s {Offers:{Recipes:[]}}
tellraw @s {"text":"WayaCreate says: Nearby villagers have refreshed their trades! (Hopefully for the better!)","color":"green"}

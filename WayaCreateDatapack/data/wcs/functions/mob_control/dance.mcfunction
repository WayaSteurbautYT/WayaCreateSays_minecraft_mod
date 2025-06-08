# Makes nearby mobs jump around a bit.
execute as @e[type=!minecraft:player,distance=..10,type=!minecraft:item,type=!minecraft:arrow,type=!minecraft:experience_orb] at @s run effect give @s minecraft:jump_boost 2 4 true
execute as @e[type=!minecraft:player,distance=..10,type=!minecraft:item,type=!minecraft:arrow,type=!minecraft:experience_orb] at @s run particle minecraft:note ~ ~1 ~ 0.5 0.5 0.5 0.1 10
tellraw @s {"text":"WayaCreate says: Everybody dance now!","color":"light_purple"}

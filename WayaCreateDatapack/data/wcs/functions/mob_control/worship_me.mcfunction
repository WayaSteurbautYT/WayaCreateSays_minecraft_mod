# Makes nearby villagers and piglins look at the player and maybe play a sound or particle.
execute as @e[type=minecraft:villager,distance=..10] at @s run effect give @s minecraft:glowing 5 0 true
execute as @e[type=minecraft:villager,distance=..10] at @s run particle minecraft:happy_villager ~ ~1 ~ 0.5 0.5 0.5 0.1 10
execute as @e[type=minecraft:piglin,distance=..10,nbt={IsAdmiring:0b}] at @s run data merge entity @s {IsAdmiring:1b}
execute as @e[type=minecraft:piglin,distance=..10] at @s run particle minecraft:heart ~ ~1 ~ 0.5 0.5 0.5 0.1 5
tellraw @s {"text":"WayaCreate says: Local villagers and piglins seem quite impressed!","color":"yellow"}

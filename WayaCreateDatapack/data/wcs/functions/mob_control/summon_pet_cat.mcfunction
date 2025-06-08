# CatType can be randomized or chosen. For now, default black.
# Available CatType values: 0 (tabby), 1 (black), 2 (red), 3 (siamese), 4 (british shorthair), 5 (calico), 6 (persian), 7 (ragdoll), 8 (white), 9 (jellie), 10 (all black).
execute at @s run summon minecraft:cat ~ ~ ~ {CatType:1}
execute store result entity @e[type=minecraft:cat,distance=..2,limit=1,sort=nearest] Owner set from entity @s UUID
tellraw @s {"text":"WayaCreate says: A sleek cat appears by your side!","color":"lime"}

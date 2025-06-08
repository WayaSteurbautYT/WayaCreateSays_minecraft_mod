scoreboard players add @s wcs.temp 0
execute store result score #has_planks wcs.temp run clear @s minecraft:oak_planks 0
execute if score #has_planks wcs.temp matches 4.. run clear @s minecraft:oak_planks 4
execute if score #has_planks wcs.temp matches 4.. run give @s minecraft:crafting_table 1
execute if score #has_planks wcs.temp matches 4.. run tellraw @s {"text":"WayaCreate says: A crafting table has been provided!","color":"gold"}
execute if score #has_planks wcs.temp matches ..3 run tellraw @s {"text":"WayaCreate says: You need at least 4 oak planks to make a crafting table!","color":"red"}

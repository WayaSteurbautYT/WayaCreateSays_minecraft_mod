execute if score @s wcs.progressStage matches 0 run {
    scoreboard players set @s wcs.progressStage 1,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: You got wood! Excellent! Check your Guidebook for the next step!","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:crafting_scroll,
    recipe give @s wcs:lumberjack_aid,
    execute store success score @s wcs.temp run clear @s minecraft:carrot_on_a_stick{wcs_item:"guidebook"} 0,
    execute if score @s wcs.temp matches 0 run give @s minecraft:carrot_on_a_stick{display:{Name:'{\"text\":\"WayaCreate\\\'s Guidebook\",\"color\":\"gold\",\"italic\":false}',Lore:['{\"text\":\"Right-click to get guidance on your journey!\",\"color\":\"yellow\"}']},wcs_item:\"guidebook\",CustomModelData:1} 1
}

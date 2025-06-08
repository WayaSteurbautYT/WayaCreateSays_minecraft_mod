execute if score @s wcs.progressStage matches 10 run {
    scoreboard players set @s wcs.progressStage 11,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Diamond Pickaxe crafted! Now you can mine Obsidian. See your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

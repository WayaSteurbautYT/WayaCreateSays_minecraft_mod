execute if score @s wcs.progressStage matches 3 run {
    scoreboard players set @s wcs.progressStage 4,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Stone pickaxe acquired! Much sturdier. Check your Guidebook!","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

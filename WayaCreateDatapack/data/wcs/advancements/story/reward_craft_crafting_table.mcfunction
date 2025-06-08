execute if score @s wcs.progressStage matches 1 run {
    scoreboard players set @s wcs.progressStage 2,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Crafting table made! You're a natural! Check your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

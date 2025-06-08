execute if score @s wcs.progressStage matches 11 run {
    scoreboard players set @s wcs.progressStage 12,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: 10 Obsidian collected! You're ready to build a gateway to another dimension!","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

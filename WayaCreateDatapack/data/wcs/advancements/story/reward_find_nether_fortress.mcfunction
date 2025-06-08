execute if score @s wcs.progressStage matches 13 run {
    scoreboard players set @s wcs.progressStage 14,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: You found a Nether Fortress! Watch out for Blazes and Wither Skeletons. Guidebook updated.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

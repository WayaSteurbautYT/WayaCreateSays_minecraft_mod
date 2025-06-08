execute if score @s wcs.progressStage matches 16 run {
    scoreboard players set @s wcs.progressStage 17,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Stronghold found! Now search for the End Portal room within. Guidebook updated.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
}

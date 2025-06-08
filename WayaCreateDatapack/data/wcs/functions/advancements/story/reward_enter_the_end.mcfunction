execute if score @s wcs.progressStage matches 18 run {
    scoreboard players set @s wcs.progressStage 19,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: You're in The End! Destroy those crystals, then fight the Dragon!","color":"light_purple"},
    title @s title {"text":"Welcome to The End!","color":"dark_purple"}
}

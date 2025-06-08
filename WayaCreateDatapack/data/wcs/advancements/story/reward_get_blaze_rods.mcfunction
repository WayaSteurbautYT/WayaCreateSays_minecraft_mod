execute if score @s wcs.progressStage matches 14 run {
    scoreboard players set @s wcs.progressStage 15,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: 5 Blaze Rods secured! These are key to reaching The End. See your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:item_placeholder_eye_of_ender_guide
}

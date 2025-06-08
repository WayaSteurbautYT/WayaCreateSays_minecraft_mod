execute if score @s wcs.progressStage matches 8 run {
    scoreboard players set @s wcs.progressStage 9,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Looking sharp and well-protected in your iron gear! Time for the next hunt. See your Guidebook.","color":"light_purple"},
    title @s title {"text":"Goal Achieved!","color":"green"},
    recipe give @s wcs:item_placeholder_diamond_dowser
}

execute if score @s wcs.progressStage matches 17 run {
    scoreboard players set @s wcs.progressStage 18,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Portal ready (or you're about to make it so)! The End awaits. Steel yourself! Guidebook updated.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:item_placeholder_dragon_slayers_brew
}

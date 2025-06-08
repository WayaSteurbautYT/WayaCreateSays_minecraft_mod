execute if score @s wcs.progressStage matches 9 run {
    scoreboard players set @s wcs.progressStage 10,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: A Diamond! Excellent find! Check your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:item_placeholder_nether_portal_kit
}

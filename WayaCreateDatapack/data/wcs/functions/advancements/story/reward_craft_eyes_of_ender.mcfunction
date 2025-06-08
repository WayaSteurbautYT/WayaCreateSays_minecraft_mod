execute if score @s wcs.progressStage matches 15 run {
    scoreboard players set @s wcs.progressStage 16,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Eyes of Ender crafted! Time to find that Stronghold. Guidebook updated.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:item_placeholder_divining_rod
}

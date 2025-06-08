execute if score @s wcs.progressStage matches 5 run {
    scoreboard players set @s wcs.progressStage 6,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Shelter established! Safe and sound... for now. See your Guidebook!","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:item_placeholder_hunters_jerky
}

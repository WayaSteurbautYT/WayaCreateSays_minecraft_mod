execute if score @s wcs.progressStage matches 7 run {
    scoreboard players set @s wcs.progressStage 8,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] Simon says: Iron Ingot acquired! Stronger tools and armor await. See your Guidebook!","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"},
    recipe give @s wcs:hearthstone,
    recipe give @s wcs:item_placeholder_iron_tools_guide
}

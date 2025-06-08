execute if score @s wcs.progressStage matches 4 run {
    scoreboard players set @s wcs.progressStage 5,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Torches crafted! Darkness, be gone! See your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
    # Placeholder: recipe give @s wcs:item_placeholder_shelter_guide
}

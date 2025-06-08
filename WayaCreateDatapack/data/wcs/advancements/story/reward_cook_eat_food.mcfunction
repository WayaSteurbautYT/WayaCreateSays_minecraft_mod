execute if score @s wcs.progressStage matches 6 run {
    scoreboard players set @s wcs.progressStage 7,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Aah, a satisfying meal! Energy restored. Check your Guidebook.","color":"light_purple"},
    title @s title {"text":"Task Complete!","color":"green"}
    # Placeholder: recipe give @s wcs:item_placeholder_iron_guide
}

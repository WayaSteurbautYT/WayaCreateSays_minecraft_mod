execute if score @s wcs.progressStage matches 2 run {
    scoreboard players set @s wcs.progressStage 3,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: All wooden tools crafted! You're getting the hang of this!","color":"light_purple"},
    title @s title {"text":"Goal Achieved!","color":"green"}
    # Placeholder: recipe give @s wcs:item_placeholder_stone_miner_charm
}

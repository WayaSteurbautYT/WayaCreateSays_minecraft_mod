execute if score @s wcs.progressStage matches 12 run {
    scoreboard players set @s wcs.progressStage 13,
    tellraw @s {"text":"[GUIDEBOOK UPDATED] WayaCreate says: Welcome to the Nether! A dangerous but rewarding place. See your Guidebook.","color":"light_purple"},
    title @s title {"text":"Goal Achieved!","color":"green"},
    recipe give @s wcs:item_placeholder_fortress_finder,
    recipe give @s wcs:item_placeholder_pact_of_friendship
}

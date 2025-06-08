# WayaCreate's Guidebook - Main Dispatcher
execute if score @s wcs.progressStage matches 0 run function wcs:guidance/stage_0_intro
execute if score @s wcs.progressStage matches 1 run function wcs:guidance/stage_1_get_wood
execute if score @s wcs.progressStage matches 2 run function wcs:guidance/stage_2_craft_tools
execute if score @s wcs.progressStage matches 3 run function wcs:guidance/stage_3_get_stone
execute if score @s wcs.progressStage matches 4 run function wcs:guidance/stage_4_find_coal
execute if score @s wcs.progressStage matches 5 run function wcs:guidance/stage_5_build_shelter
execute if score @s wcs.progressStage matches 6 run function wcs:guidance/stage_6_get_food
execute if score @s wcs.progressStage matches 7 run function wcs:guidance/stage_7_find_iron
execute if score @s wcs.progressStage matches 8 run function wcs:guidance/stage_8_craft_iron_gear
execute if score @s wcs.progressStage matches 9 run function wcs:guidance/stage_9_find_diamonds
execute if score @s wcs.progressStage matches 10 run function wcs:guidance/stage_10_craft_diamond_pickaxe
execute if score @s wcs.progressStage matches 11 run function wcs:guidance/stage_11_mine_obsidian
execute if score @s wcs.progressStage matches 12 run function wcs:guidance/stage_12_build_nether_portal
execute if score @s wcs.progressStage matches 13 run function wcs:guidance/stage_13_find_fortress
execute if score @s wcs.progressStage matches 14 run function wcs:guidance/stage_14_get_blaze_rods
execute if score @s wcs.progressStage matches 15 run function wcs:guidance/stage_15_craft_eyes_of_ender
execute if score @s wcs.progressStage matches 16 run function wcs:guidance/stage_16_locate_stronghold
execute if score @s wcs.progressStage matches 17 run function wcs:guidance/stage_17_activate_end_portal
execute if score @s wcs.progressStage matches 18 run function wcs:guidance/stage_18_enter_the_end
execute if score @s wcs.progressStage matches 19 run function wcs:guidance/stage_19_defeat_ender_dragon
# Fallback for stages not yet fully implemented (e.g. post-dragon)
execute if score @s wcs.progressStage matches 20.. unless score @s wcs.progressStage matches 100 run tellraw @s {"text":"You consult the Guidebook... More guidance coming soon for this stage!","color":"yellow"}
execute if score @s wcs.progressStage matches 100 run function wcs:guidance/stage_complete_game # Placeholder for now

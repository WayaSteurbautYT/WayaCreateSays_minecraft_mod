tellraw @s ""
tellraw @s {"text":"--- WayaCreate Says - Chat Commands ---","color":"gold"}
tellraw @s {"text":"Click a command below or type it. You may need to type it once to enable it.","color":"yellow"}
tellraw @s ["",{"text":"[Help]","color":"green","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_help set 1"},"hoverEvent":{"action":"show_text","contents":"Show this help message"}}]
tellraw @s ["",{"text":"[Give Diamonds]","color":"aqua","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_giveDia set 1"},"hoverEvent":{"action":"show_text","contents":"Get a stack of diamonds"}}]
tellraw @s ["",{"text":"[Time Stop]","color":"aqua","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_timeStop set 1"},"hoverEvent":{"action":"show_text","contents":"Freeze time at noon"}}]
tellraw @s ["",{"text":"[Time Resume]","color":"aqua","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_timeResume set 1"},"hoverEvent":{"action":"show_text","contents":"Let time flow normally"}}]
tellraw @s ["",{"text":"[WayaCreate Mode ON]","color":"red","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_wcModeOn set 1"},"hoverEvent":{"action":"show_text","contents":"Activate WayaCreate Mode"}}]
tellraw @s ["",{"text":"[WayaCreate Mode OFF]","color":"red","clickEvent":{"action":"suggest_command","value":"/trigger wcs.cmd_wcModeOff set 1"},"hoverEvent":{"action":"show_text","contents":"Deactivate WayaCreate Mode"}}]

# Enable triggers for the player so they can click them
scoreboard players enable @s wcs.cmd_help
scoreboard players enable @s wcs.cmd_giveDia
scoreboard players enable @s wcs.cmd_timeStop
scoreboard players enable @s wcs.cmd_timeResume
scoreboard players enable @s wcs.cmd_wcModeOn
scoreboard players enable @s wcs.cmd_wcModeOff

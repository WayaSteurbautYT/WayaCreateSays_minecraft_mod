# Process Help Command
execute as @a[scores={wcs.cmd_help=1..}] run function wcs:chat_commands/main
execute as @a[scores={wcs.cmd_help=1..}] run scoreboard players set @s wcs.cmd_help 0

# Process Give Diamonds
execute as @a[scores={wcs.cmd_giveDia=1..}] run function wcs:game_control/give_diamonds_cheat
execute as @a[scores={wcs.cmd_giveDia=1..}] run scoreboard players enable @s wcs.cmd_giveDia
execute as @a[scores={wcs.cmd_giveDia=1..}] run scoreboard players set @s wcs.cmd_giveDia 0

# Process Time Stop
execute as @a[scores={wcs.cmd_timeStop=1..}] run function wcs:game_control/time_stop
execute as @a[scores={wcs.cmd_timeStop=1..}] run scoreboard players enable @s wcs.cmd_timeStop
execute as @a[scores={wcs.cmd_timeStop=1..}] run scoreboard players set @s wcs.cmd_timeStop 0

# Process Time Resume
execute as @a[scores={wcs.cmd_timeResume=1..}] run function wcs:game_control/time_resume
execute as @a[scores={wcs.cmd_timeResume=1..}] run scoreboard players enable @s wcs.cmd_timeResume
execute as @a[scores={wcs.cmd_timeResume=1..}] run scoreboard players set @s wcs.cmd_timeResume 0

# Process WayaCreate Mode ON
execute as @a[scores={wcs.cmd_wcModeOn=1..}] run function wcs:game_control/init_wayacreate_mode
execute as @a[scores={wcs.cmd_wcModeOn=1..}] run scoreboard players enable @s wcs.cmd_wcModeOn
execute as @a[scores={wcs.cmd_wcModeOn=1..}] run scoreboard players set @s wcs.cmd_wcModeOn 0

# Process WayaCreate Mode OFF
execute as @a[scores={wcs.cmd_wcModeOff=1..}] run function wcs:game_control/remove_wayacreate_mode
execute as @a[scores={wcs.cmd_wcModeOff=1..}] run scoreboard players enable @s wcs.cmd_wcModeOff
execute as @a[scores={wcs.cmd_wcModeOff=1..}] run scoreboard players set @s wcs.cmd_wcModeOff 0

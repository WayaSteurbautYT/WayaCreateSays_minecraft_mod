execute in minecraft:overworld run locate structure minecraft:stronghold
# The above command only outputs to server console/logs, not directly to player chat in a usable way via function alone.
# We need a helper entity or tellraw trick to show the coordinates to the player.
# For now, just a message saying it was located (admin would see coords in log).
tellraw @s {"text":"WayaCreate says: Attempting to locate stronghold... (Admins: check server log for coords). A better system for this is needed.","color":"dark_aqua"}

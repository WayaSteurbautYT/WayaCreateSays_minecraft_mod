# Starts villager worship
say @s is now worshipped by villagers!

function wcs:worship/build_villager_statue

tag @s add is_villager_worshipped
# Optional: Could also tag nearby villagers or the village itself if a location is determined

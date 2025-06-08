# Trusted requires actual UUIDs.
# A simpler approach for "pet-like" behavior without full taming if direct Owner tag isn't simple:
# execute at @s run summon minecraft:fox ~ ~ ~ {CustomName:'{"text":"Player\'s Fox","color":"gold"}', NoAI:0b, PersistenceRequired:1b}
# Add to a specific "pet_foxes" team that doesn't fight players and maybe follows them via other means.
# For now, just summon a friendly-named fox.
# Simpler: Just summon a fox. True taming is complex for foxes via commands.
execute at @s run summon minecraft:fox ~ ~ ~ {CustomNameVisible:1b, CustomName:'{"text":"WayaCreate\'s Fox"}'}
tellraw @s {"text":"WayaCreate says: A cunning fox appears! It seems friendly.","color":"lime"}

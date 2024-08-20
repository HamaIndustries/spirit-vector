package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class SpiritVectorLoot {
    public static void init() {
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && key.equals(LootTables.ANCIENT_CITY_CHEST)){
                var builder = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0, 1));
                SpiritVectorItems.getSfxUpgradeItems().forEach(
                        item -> builder.with(ItemEntry.builder(item))
                );
                tableBuilder.pool(builder);
            }

            if (source.isBuiltin() && key.equals(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY)){
                var builder = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0, 1));
                SpiritVectorItems.getDreamRunes().forEach(
                        item -> builder.with(ItemEntry.builder(item))
                );
                tableBuilder.pool(builder);
            }
        }));
    }
}

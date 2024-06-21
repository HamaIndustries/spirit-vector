package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.fabricmc.fabric.mixin.recipe.ingredient.IngredientMixin;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.item.DreamRuneItem;
import symbolics.division.spirit.vector.item.SlotTemplateItem;
import symbolics.division.spirit.vector.item.SpiritVectorItem;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpiritVectorDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(SVModelGenerator::new);
		pack.addProvider(SVRecipeGenerator::new);
	}



	private static class SVModelGenerator extends FabricModelProvider {

		public SVModelGenerator(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			for (Item item : SpiritVectorItems.getGeneratedItems()) {
				itemModelGenerator.register(item, Models.GENERATED);
			}
		}
	}

	private static class SVRecipeGenerator extends FabricRecipeProvider {

		public SVRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void generate(RecipeExporter exporter) {
			// not enough time for good recipes sorry
			DreamRuneItem[] runes = SpiritVectorItems.getDreamRunes().toArray(DreamRuneItem[]::new);

			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.LEFT_SLOT_TEMPLATE);
			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.RIGHT_SLOT_TEMPLATE);
			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.UP_SLOT_TEMPLATE);
		}

		void genSlotTemplateUpgrade(RecipeExporter exporter, DreamRuneItem[] runes, SlotTemplateItem slot) {
			String name;
			try {
				 name = slot.getDefaultStack().get(AbilitySlot.COMPONENT).name;
			} catch (NullPointerException x) {
				// record scratch freeze frame
				throw new RuntimeException("You probably tried to make a recipe from an invalid slot template stack");
			}
			SmithingTrimRecipeJsonBuilder.create(
					Ingredient.ofItems(slot),
					Ingredient.ofItems(SpiritVectorItems.SPIRIT_VECTOR),
					Ingredient.ofItems(runes),
					RecipeCategory.TRANSPORTATION
			).criterion("has_spirit_vector_slot_template_" + name, conditionsFromItem(slot))
			.offerTo(exporter, SpiritVectorMod.id(name + "_slot_ability"));
		}
	}
}

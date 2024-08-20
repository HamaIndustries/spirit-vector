package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.item.DreamRuneItem;
import symbolics.division.spirit.vector.item.SlotTemplateItem;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.sfx.SFXPack;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SpiritVectorDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(SVModelGenerator::new);
		pack.addProvider(SVItemTagGenerator::new);
		pack.addProvider(SVRecipeGenerator::new);
		pack.addProvider(SVSoundTagGenerator::new);
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

	private static class SVItemTagGenerator extends FabricTagProvider.ItemTagProvider {

		public SVItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(SpiritVectorTags.Items.SFX_PACK_ADDITIONS)
					.add(Items.DIAMOND);

			getOrCreateTagBuilder(SpiritVectorTags.Items.SFX_PACK_TEMPLATES)
					.add(SpiritVectorItems.getSfxUpgradeItems().toArray(Item[]::new));

			getOrCreateTagBuilder(SpiritVectorTags.Items.SLOT_UPGRADE_RUNES)
					.add(SpiritVectorItems.LEFT_SLOT_TEMPLATE)
					.add(SpiritVectorItems.UP_SLOT_TEMPLATE)
					.add(SpiritVectorItems.RIGHT_SLOT_TEMPLATE);

			getOrCreateTagBuilder(SpiritVectorTags.Items.ABILITY_UPGRADE_RUNES)
					.add(SpiritVectorItems.getDreamRunes().toArray(Item[]::new));
		}
	}

	private static class SVSoundTagGenerator extends FabricTagProvider<SoundEvent> {
		public SVSoundTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, RegistryKeys.SOUND_EVENT, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(SpiritVectorTags.Misc.JUKEBOX_LOOPING)
					.add(SpiritVectorSounds.TAKE_BREAK_LOOP)
					.add(SpiritVectorSounds.SHOW_DONE_LOOP);
		}
	}

	private static class SVRecipeGenerator extends FabricRecipeProvider {

		public SVRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void generate(RecipeExporter exporter) {

			DreamRuneItem[] runes = SpiritVectorItems.getDreamRunes().toArray(DreamRuneItem[]::new);

			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.LEFT_SLOT_TEMPLATE);
			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.RIGHT_SLOT_TEMPLATE);
			genSlotTemplateUpgrade(exporter, runes, SpiritVectorItems.UP_SLOT_TEMPLATE);

			SmithingTrimRecipeJsonBuilder.create(
							Ingredient.fromTag(SpiritVectorTags.Items.SFX_PACK_TEMPLATES),
							Ingredient.ofItems(SpiritVectorItems.SPIRIT_VECTOR),
							Ingredient.fromTag(SpiritVectorTags.Items.SFX_PACK_ADDITIONS),
							RecipeCategory.TRANSPORTATION
					).criterion("has_spirit_vector", conditionsFromItem(SpiritVectorItems.SPIRIT_VECTOR))
					.offerTo(exporter, SpiritVectorMod.id("sfx_alchemy"));

			// sv per-core recipes
			for (var core : SpiritVectorItems.getSfxUpgradeItems()) {
				Identifier recipeId = Identifier.of(RecipeProvider.getItemPath(SpiritVectorItems.SPIRIT_VECTOR) + "_crafted_from_" + RecipeProvider.getItemPath(core));
				ItemStack stack = SpiritVectorItems.SPIRIT_VECTOR.getDefaultStack();
				stack.set(SFXPack.COMPONENT, core.getComponents().get(SFXPack.COMPONENT));
				Advancement.Builder builder = exporter.getAdvancementBuilder()
						.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
						.rewards(AdvancementRewards.Builder.recipe(recipeId))
						.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
				builder.criterion("has_core", conditionsFromItem(core));
				RawShapedRecipe rawRecipe = RawShapedRecipe.create(
						Map.of('g', Ingredient.ofItems(Items.GOLD_INGOT), 'c', Ingredient.ofItems(core)),
						"gcg",
						"g g"
				);
				ShapedRecipe recipe = new ShapedRecipe(
						"",
						CraftingRecipeJsonBuilder.toCraftingCategory(RecipeCategory.TRANSPORTATION),
						rawRecipe,
						stack.copy(),
						true
				);
				exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + RecipeCategory.TRANSPORTATION.getName() + "/")));
			}
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
					Ingredient.fromTag(SpiritVectorTags.Items.SLOT_UPGRADE_RUNES),
					Ingredient.ofItems(SpiritVectorItems.SPIRIT_VECTOR),
					Ingredient.fromTag(SpiritVectorTags.Items.ABILITY_UPGRADE_RUNES),
					RecipeCategory.TRANSPORTATION
			).criterion("has_spirit_vector_slot_template_" + name, conditionsFromItem(slot))
			.offerTo(exporter, SpiritVectorMod.id(name + "_slot_ability"));
		}
	}
}

package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.fabric.mixin.recipe.ingredient.IngredientMixin;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.jukebox.JukeboxSongs;
import net.minecraft.data.client.*;
import net.minecraft.data.server.DynamicRegistriesProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
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
			// not enough time for good recipes sorry
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

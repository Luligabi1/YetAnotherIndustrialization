package me.luligabi.yet_another_industrialization.common.util

import me.luligabi.yet_another_industrialization.common.YAI
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTranslatableTextEnum

enum class YAIText(private val text: String): MICompatibleTranslatableTextEnum {

    DIAGNOSER_TITLE("%s's Diagnosis (hover for details):\n"),
    DIAGNOSER_TITLE_NONE("No issues found with %s"),

    DIAGNOSER_NO_STEAM("Missing Steam"),
    DIAGNOSER_NO_STEAM_DESCRIPTION("Machine has insufficient Steam to proceed"),

    DIAGNOSER_NO_ENERGY("Missing Energy"),
    DIAGNOSER_NO_ENERGY_DESCRIPTION("This machine requires energy to function"),
    DIAGNOSER_NO_ENERGY_WARNING_DESCRIPTION("This machine might require energy to function"),

    DIAGNOSER_NO_ENERGY_INPUT("Missing Energy Input"),
    DIAGNOSER_NO_ENERGY_INPUT_DESCRIPTION("This machine should have a energy input hatch."),

    DIAGNOSER_NO_ENERGY_OUTPUT("Missing Energy Output"),
    DIAGNOSER_NO_ENERGY_OUTPUT_DESCRIPTION("This machine should have a energy output hatch."),

    DIAGNOSER_NO_RECIPE("No available recipes"),
    DIAGNOSER_NO_RECIPE_DESCRIPTION("There are no available recipes using the current ingredients. Check if you're missing something."),

    DIAGNOSER_BANNED_RECIPE("Recipe available, but not usable"),
    DIAGNOSER_BANNED_RECIPE_DESCRIPTION("A recipe is available, but unusable. This most likely means that: \n\n- You need more Upgrades to use this recipe\n- The recipe requires a higher multiblock tier (i.e. Electric Blast Furnace's Kanthal Coils)"),

    DIAGNOSER_CANT_TAKE_ITEM_INPUT("Can't take Item Input"),
    DIAGNOSER_CANT_TAKE_ITEM_INPUT_DESCRIPTION("The machine either has nowhere to take items from or they're insufficient to repeat the current recipe."),

    DIAGNOSER_CANT_TAKE_FLUID_INPUT("Can't take Fluid Input"),
    DIAGNOSER_CANT_TAKE_FLUID_INPUT_DESCRIPTION("The machine either has nowhere to take fluids from or they're insufficient to repeat the current recipe."),

    DIAGNOSER_CANT_PUT_ITEM_OUTPUT("Can't put Item Output"),
    DIAGNOSER_CANT_PUT_ITEM_OUTPUT_DESCRIPTION("The machine has nowhere to put item outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"),

    DIAGNOSER_CANT_PUT_FLUID_OUTPUT("Can't put Fluid Output"),
    DIAGNOSER_CANT_PUT_FLUID_OUTPUT_DESCRIPTION("The machine has nowhere to put fluids outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"),

    DIAGNOSER_UNMET_CONDITION("Unmet recipe conditions"),
    DIAGNOSER_UNMET_CONDITION_DESCRIPTION("Machine does not meet one or more of the recipe's conditions"),

    DIAGNOSER_INVALID_MULTIBLOCK_SHAPE("Invalid Multiblock Shape"),
    DIAGNOSER_INVALID_MULTIBLOCK_SHAPE_DESCRIPTION("The multiblock is misbuilt. Hold a Wrench to preview the correct shape. If the multiblock has tiers, check if you've selected the right one."),

    DIAGNOSER_NO_LARGE_TANK_HATCH("Missing Large Tank Hatches"),
    DIAGNOSER_NO_LARGE_TANK_HATCH_DESCRIPTION("Large Tanks require Large Tank Hatches"),

    MACHINE_REMOVER_INSUFFICIENT_ENERGY("Insufficient energy"),
    MACHINE_REMOVER_TOO_LARGE("Too large to remove!"),

    ARBOREOUS_GREENHOUSE_TIER_TOOLTIP("Requires %s soil"),

    ENERGY_GENERATION_TOOLTIP_1("Generates %s"),
    ENERGY_GENERATION_TOOLTIP_2("Ignore what EMI/JEI is telling you, this doesn't use energy."),


    GUIDEBOOK_TOOLTIP("Same pronunciation as \"YAY\"!");


    override fun englishText() = text

    override fun getTranslationKey() = "text.${YAI.ID}.${this.name.lowercase()}"

}
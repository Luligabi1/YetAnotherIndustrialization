package me.luligabi.yet_another_industrialization.common.util

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.chat.MutableComponent
import net.swedz.tesseract.neoforge.lang.annotation.LangKey;
import net.swedz.tesseract.neoforge.lang.annotation.WithStyle

interface YAIText {

    /** Machine Diagnoser */
    @LangKey("{}'s Diagnosis (hover for details):\n")
    fun diagnoserTitle(machine: String): MutableComponent
    @LangKey("No issues found with {}")
    fun diagnoserTitleNone(machine: String): MutableComponent

    @LangKey("Missing Steam")
    fun diagnoserNoSteam(): MutableComponent
    @LangKey("This machine requires steam to function")
    fun diagnoserNoSteamDescription(): MutableComponent

    @LangKey("Missing Energy")
    fun diagnoserNoEnergy(): MutableComponent
    @LangKey("This machine requires energy to function")
    fun diagnoserNoEnergyDescription(): MutableComponent
    @LangKey("This machine might require energy to function")
    fun diagnoserNoEnergyWarningDescription(): MutableComponent

    @LangKey("Missing Energy Input")
    fun diagnoserNoEnergyInput(): MutableComponent
    @LangKey("This machine should have an energy input hatch.")
    fun diagnoserNoEnergyInputDescription(): MutableComponent

    @LangKey("Missing Energy Output")
    fun diagnoserNoEnergyOutput(): MutableComponent
    @LangKey("This machine should have an energy output hatch.")
    fun diagnoserNoEnergyOutputDescription(): MutableComponent

    @LangKey("No available recipes")
    fun diagnoserNoRecipe(): MutableComponent
    @LangKey("There are no available recipes using the current ingredients. Check if you're missing something.")
    fun diagnoserNoRecipeDescription(): MutableComponent

    @LangKey("Recipe available, but not usable")
    fun diagnoserBannedRecipe(): MutableComponent
    @LangKey("A recipe is available, but unusable. This most likely means that: \n\n- You need more Upgrades to use this recipe\n- The recipe requires a higher multiblock tier (i.e. Electric Blast Furnace's Kanthal Coils)")
    fun diagnoserBannedRecipeDescription(): MutableComponent

    @LangKey("Can't take Item Input")
    fun diagnoserCantTakeItemInput(): MutableComponent
    @LangKey("This machine either has nowhere to take items from or they're insufficient to repeat the current recipe.")
    fun diagnoserCantTakeItemInputDescription(): MutableComponent

    @LangKey("Can't take Fluid Input")
    fun diagnoserCantTakeFluidInput(): MutableComponent
    @LangKey("The machine either has nowhere to take fluids from or they're insufficient to repeat the current recipe.")
    fun diagnoserCantTakeFluidInputDescription(): MutableComponent

    @LangKey("Can't put Item Output")
    fun diagnoserCantPutItemOutput(): MutableComponent
    @LangKey("The machine has nowhere to put item outputs in. This might mean that: \\n\\n- There's no available slot\\n- The slot's capacity isn't enough to store the output")
    fun diagnoserCantPutItemOutputDescription(): MutableComponent

    @LangKey("Can't put Fluid Output")
    fun diagnoserCantPutFluidOutput(): MutableComponent
    @LangKey("The machine has nowhere to put fluids outputs in. This might mean that: \\n\\n- There's no available slot\\n- The slot's capacity isn't enough to store the output")
    fun diagnoserCantPutFluidOutputDescription(): MutableComponent

    @LangKey("Unmet Recipe Conditions")
    fun diagnoserUnmetCondition(): MutableComponent
    @LangKey("The current recipe has condition(s) that are not being met.")
    fun diagnoserUnmetConditionDescription(): MutableComponent

    @LangKey("Invalid Multiblock Shape")
    fun diagnoserInvalidMultiblockShape(): MutableComponent
    @LangKey("The multiblock is misbuilt. Hold a Wrench to preview the correct shape. Also check:\n\n- If the multiblock has tiers, select the right one on the GUI;\n- Some multiblocks, like the Pressurizer, are picky about where each hatch type is placed.")
    fun diagnoserInvalidMultiblockShapeDescription(): MutableComponent

    @LangKey("Redstone Blocked")
    fun diagnoserRedstoneBlocked(): MutableComponent
    @LangKey("The machine's Redstone Control Module requires the opposite signal to allow operation.\\nInvert the signal or remove the module to proceed.")
    fun diagnoserRedstoneBlockedDescription(): MutableComponent

    @LangKey("Missing Large Tank Hatches")
    fun diagnoserNoLargeTankHatch(): MutableComponent
    @LangKey("Large Tanks require Large Tank Hatches")
    fun diagnoserNoLargeTankHatchDiagnoser(): MutableComponent

    @LangKey("Unsupported machine")
    fun diagnoserUnsupported(): MutableComponent
    @LangKey("Sorry, I can't diagnose any of this machine's specific behaviors :(\n\nI really need to beat Statech Industry...")
    fun diagnoserUnsupportedDescription(): MutableComponent

    @LangKey("Diagnoses common issues with machines")
    fun diagnoserTooltip1(): MutableComponent

    /** Machine Remover */
    @LangKey("Insufficient Energy")
    fun machineRemoverInsufficientEnergy(): MutableComponent
    @LangKey("Too large to remove!")
    fun machineRemoverTooLarge(): MutableComponent
    @LangKey("Sorry can't remove this type of machine :(")
    fun machineRemoverBanned(): MutableComponent

    @LangKey("Removes machines in a single click")
    fun machineRemoverTooltip1(): MutableComponent
    @LangKey("Single Block Remove Cost: {}")
    fun machineRemoverTooltip2(cost: String): MutableComponent
    @LangKey("Multiblock Remove Cost: {}")
    fun machineRemoverTooltip3(cost: String): MutableComponent


    /** Slot Locker */
    @LangKey("Set Mode to {}")
    fun slotLockerModeChange(mode: String): MutableComponent
    @LangKey("Mode: {}")
    fun slotLockerModePrefix(mode: String): MutableComponent
    @LangKey("Lock Item Slots")
    fun slotLockerModeLockItem(): MutableComponent
    @LangKey("Lock Fluid Slots")
    fun slotLockerModeLockFluid(): MutableComponent
    @LangKey("Lock Both")
    fun slotLockerModeLockBoth(): MutableComponent
    @LangKey("Unlock Item Slots")
    fun slotLockerModeUnlockItem(): MutableComponent
    @LangKey("Unlock Fluid Slots")
    fun slotLockerModeUnlockFluid(): MutableComponent
    @LangKey("Unlock Both")
    fun slotLockerModeUnlockBoth(): MutableComponent
    @LangKey("{} | {}")
    fun slotLockerTooltip1(a: String, b: String): MutableComponent
    @LangKey("Drag an item and a fluid from your recipe viewer to select them as the desired lock")
    fun slotLockerTooltip2(): MutableComponent
    @LangKey("Sneak + Scroll")
    fun slotLockerTooltip3Instruction(): MutableComponent
    @LangKey("{} to change mode")
    fun slotLockerTooltip3Suffix(key: String): MutableComponent


    /** Arboreous Greenhouse */
    @LangKey("Requires {} soil")
    @WithStyle("")
    fun arboreousGreenhouseTierTooltip(soil: String): MutableComponent

    /** Dragon Egg Siphon */
    @LangKey("Generates {}")
    fun energyGenerationTooltip1(energy: String): MutableComponent

    /** Large Storage Unit */
    @LangKey("Not linked to a Large Storage Unit")
    fun noLargeStorageUnit(): MutableComponent
    @LangKey("Place an item to charge here!")
    fun chargingSlotTooltip(): MutableComponent
    @LangKey("Large Storage Unit Tiers")
    fun largeStorageUnitTiers(): MutableComponent
    @LangKey("Capacity: {}{} EU")
    fun largeStorageUnitTierCapacity(stored: String, capacity: String): MutableComponent
    @LangKey("Can I/O to its voltage and lower")
    fun largeStorageUnitTooltip1(): MutableComponent
    @LangKey("i.e. At HV, it's capable of interacting with HV, MV, and LV!")
    fun largeStorageUnitTooltip2(): MutableComponent


    /** Hatch Names */
    @LangKey("Mixed Input Hatch")
    fun mixedInputHatch(): MutableComponent
    @LangKey("Mixed Output Hatch")
    fun mixedOutputHatch(): MutableComponent

    /** Misc. Item tooltips */
    @LangKey("Pronounced as \"YAY\"!")
    fun guidebookTooltip(): MutableComponent
    @LangKey("The Brazilian Spirit")
    fun cachacaTooltip(): MutableComponent
    @LangKey("Vibe cooking is my passion")
    fun aiSlopTooltip(): MutableComponent
    @LangKey("The buns are barely capable of holding it all together")
    fun ultradenseMetalBallBurgerTooltip(): MutableComponent
    @LangKey(value = "Sneak + Right-click", key = "sneak_rc_activate_1")
    fun sneakRCActivate1(): MutableComponent
    @LangKey(value = "{} to activate", key = "sneak_rc_activate_2")
    fun sneakRCActivate2(key: String): MutableComponent
    @LangKey("None")
    fun none(): MutableComponent

}
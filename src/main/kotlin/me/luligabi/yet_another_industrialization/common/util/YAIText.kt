package me.luligabi.yet_another_industrialization.common.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.swedz.tesseract.neoforge.lang.annotation.LangKey
import net.swedz.tesseract.neoforge.lang.annotation.Parsed
import net.swedz.tesseract.neoforge.lang.annotation.WithStyle

interface YAIText {

    /** Machine Diagnoser */
    @WithStyle("gray")
    @LangKey(text = ["%s's Diagnosis (hover for details):\n"])
    fun diagnoserTitle(@WithStyle("highlight") machine: Component): MutableComponent

    @WithStyle("green_neutron")
    @LangKey(text = ["No issues found with %s"])
    fun diagnoserTitleNone(@WithStyle("highlight") machine: Component): MutableComponent

    @LangKey(text = ["Missing Steam"])
    fun diagnoserNoSteam(): MutableComponent
    @LangKey(text = ["This machine requires steam to function"])
    fun diagnoserNoSteamDescription(): MutableComponent

    @LangKey(text = ["Missing Energy"])
    fun diagnoserNoEnergy(): MutableComponent
    @LangKey(text = ["This machine requires energy to function"])
    fun diagnoserNoEnergyDescription(): MutableComponent
    @LangKey(text = ["This machine might require energy to function"])
    fun diagnoserNoEnergyWarningDescription(): MutableComponent

    @LangKey(text = ["Missing Energy Input"])
    fun diagnoserNoEnergyInput(): MutableComponent
    @LangKey(text = ["This machine should have an energy input hatch."])
    fun diagnoserNoEnergyInputDescription(): MutableComponent

    @LangKey(text = ["Missing Energy Output"])
    fun diagnoserNoEnergyOutput(): MutableComponent
    @LangKey(text = ["This machine should have an energy output hatch."])
    fun diagnoserNoEnergyOutputDescription(): MutableComponent

    @LangKey(text = ["No available recipes"])
    fun diagnoserNoRecipe(): MutableComponent
    @LangKey(text = ["There are no available recipes using the current ingredients. Check if you're missing something."])
    fun diagnoserNoRecipeDescription(): MutableComponent

    @LangKey(text = ["Recipe available, but not usable"])
    fun diagnoserBannedRecipe(): MutableComponent
    @LangKey(text = ["A recipe is available, but unusable. This most likely means that: \n\n- You need more Upgrades to use this recipe\n- The recipe requires a higher multiblock tier (i.e. Electric Blast Furnace's Kanthal Coils)"])
    fun diagnoserBannedRecipeDescription(): MutableComponent

    @LangKey(text = ["Can't take Item Input"])
    fun diagnoserCantTakeItemInput(): MutableComponent
    @LangKey(text = ["This machine either has nowhere to take items from or they're insufficient to repeat the current recipe."])
    fun diagnoserCantTakeItemInputDescription(): MutableComponent

    @LangKey(text = ["Can't take Fluid Input"])
    fun diagnoserCantTakeFluidInput(): MutableComponent
    @LangKey(text = ["The machine either has nowhere to take fluids from or they're insufficient to repeat the current recipe."])
    fun diagnoserCantTakeFluidInputDescription(): MutableComponent

    @LangKey(text = ["Can't put Item Output"])
    fun diagnoserCantPutItemOutput(): MutableComponent
    @LangKey(text = ["The machine has nowhere to put item outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"])
    fun diagnoserCantPutItemOutputDescription(): MutableComponent

    @LangKey(text = ["Can't put Fluid Output"])
    fun diagnoserCantPutFluidOutput(): MutableComponent
    @LangKey(text = ["The machine has nowhere to put fluids outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"])
    fun diagnoserCantPutFluidOutputDescription(): MutableComponent

    @LangKey(text = ["Unmet Recipe Conditions"])
    fun diagnoserUnmetCondition(): MutableComponent
    @LangKey(text = ["The current recipe has condition(s) that are not being met."])
    fun diagnoserUnmetConditionDescription(): MutableComponent

    @LangKey(text = ["Invalid Multiblock Shape"])
    fun diagnoserInvalidMultiblockShape(): MutableComponent
    @LangKey(text = ["The multiblock is misbuilt. Hold a Wrench to preview the correct shape. Also check:\n\n- If the multiblock has tiers, select the right one on the GUI;\n- Some multiblocks, like the Pressurizer, are picky about where each hatch type is placed."])
    fun diagnoserInvalidMultiblockShapeDescription(): MutableComponent

    @LangKey(text = ["Redstone Blocked"])
    fun diagnoserRedstoneBlocked(): MutableComponent
    @LangKey(text = ["The machine's Redstone Control Module requires the opposite signal to allow operation.\nInvert the signal or remove the module to proceed."])
    fun diagnoserRedstoneBlockedDescription(): MutableComponent

    @LangKey(text = ["Missing Large Tank Hatches"])
    fun diagnoserNoLargeTankHatch(): MutableComponent
    @LangKey(text = ["Large Tanks require Large Tank Hatches"])
    fun diagnoserNoLargeTankHatchDescription(): MutableComponent

    @LangKey(text = ["Unsupported machine"])
    fun diagnoserUnsupported(): MutableComponent
    @LangKey(text = ["Sorry, I can't diagnose any of this machine's specific behaviors :(\n\nI really need to beat Statech Industry..."])
    fun diagnoserUnsupportedDescription(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Diagnoses common issues with machines"])
    fun diagnoserTooltip1(): MutableComponent

    /** Machine Remover */
    @WithStyle("machine_remover")
    @LangKey(text = ["Insufficient Energy"])
    fun machineRemoverInsufficientEnergy(): MutableComponent

    @WithStyle("machine_remover")
    @LangKey(text = ["Too large to remove!"])
    fun machineRemoverTooLarge(): MutableComponent

    @WithStyle("machine_remover")
    @LangKey(text = ["Sorry, can't remove this type of machine :("])
    fun machineRemoverBanned(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Removes machines in a single click"])
    fun machineRemoverTooltip1(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Single Block Remove Cost: %s"])
    fun machineRemoverTooltip2(@WithStyle("highlight") cost: Component): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Multiblock Remove Cost: %s"])
    fun machineRemoverTooltip3(@WithStyle("highlight") cost: String): MutableComponent

    /** Slot Locker */
    @WithStyle("gray")
    @LangKey(text = ["Set Mode to %s"])
    fun slotLockerModeChange(@WithStyle("highlight") mode: Component): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Mode: %s"])
    fun slotLockerModePrefix(@WithStyle("highlight") mode: Component): MutableComponent

    @LangKey(text = ["Lock Item Slots"])
    fun slotLockerModeLockItem(): MutableComponent

    @LangKey(text = ["Lock Fluid Slots"])
    fun slotLockerModeLockFluid(): MutableComponent

    @LangKey(text = ["Lock Both"])
    fun slotLockerModeLockBoth(): MutableComponent

    @LangKey(text = ["Unlock Item Slots"])
    fun slotLockerModeUnlockItem(): MutableComponent

    @LangKey(text = ["Unlock Fluid Slots"])
    fun slotLockerModeUnlockFluid(): MutableComponent

    @LangKey(text = ["Unlock Both"])
    fun slotLockerModeUnlockBoth(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["%s | %s"])
    fun slotLockerTooltip1(item: Component, fluid: Component): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Drag an item and a fluid from your recipe viewer to select them as the desired lock"])
    fun slotLockerTooltip2(): MutableComponent

    @LangKey(text = ["Sneak + Scroll"])
    fun slotLockerTooltip3Instruction(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["%s to change mode"])
    fun slotLockerTooltip3Suffix(@WithStyle("highlight") key: Component): MutableComponent

    /** Industrialist's Goggles */
    @WithStyle("gray")
    @LangKey(text = ["Previews multiblock shapes without the need to hold a wrench"])
    fun gogglesTooltip1(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Press %s to toggle"])
    fun gogglesTooltip2(
        @Parsed("keybind") @WithStyle("highlight") keybind: String
    ): MutableComponent

    /** Arboreous Greenhouse */
    @WithStyle("gray")
    @LangKey(text = ["Requires %s soil"])
    fun arboreousGreenhouseTierTooltip(
        @WithStyle("highlight") soil: Component
    ): MutableComponent

    /** Numismatic Generator */
    @LangKey(text = ["Numismatic Currencies"])
    fun numismaticCurrencies(): MutableComponent

    @LangKey(text = ["EU per item: %d"])
    fun euPerItem(eu: Long): MutableComponent

    /** Dragon Egg Siphon */
    @WithStyle("gray")
    @LangKey(text = ["Generates %s"])
    fun energyGenerationTooltip(
        @WithStyle("highlight") energy: Component
    ): MutableComponent

    /** Large Storage Unit */
    @WithStyle("red")
    @LangKey(text = ["Not linked to a Large Storage Unit"])
    fun noLargeStorageUnit(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Place an item to charge here!"])
    fun chargingSlotTooltip(): MutableComponent

    @LangKey(text = ["Large Storage Unit Tiers"])
    fun largeStorageUnitTiers(): MutableComponent

    @LangKey(text = ["Capacity: %s %sEU"])
    fun largeStorageUnitTierCapacity(digit: String, unit: String): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Can I/O to its voltage and lower"])
    fun largeStorageUnitTooltip1(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["i.e. At HV, it's capable of interacting with HV, MV, and LV!"])
    fun largeStorageUnitTooltip2(): MutableComponent

    @LangKey(text = ["Ultimate"])
    fun largeStorageUnitUltimateTier(): MutableComponent

    /** Hatch Names */
    @LangKey(text = ["Mixed Input Hatch"])
    fun mixedInputHatch(): MutableComponent
    @LangKey(text = ["Mixed Output Hatch"])
    fun mixedOutputHatch(): MutableComponent

    /** Misc. Item tooltips */
    @WithStyle("gray_italic")
    @LangKey(text = ["Pronounced as \"YAY\"!"])
    fun guidebookTooltip(): MutableComponent

    @WithStyle("gray_italic")
    @LangKey(text = ["The Brazilian Spirit"])
    fun cachacaTooltip(): MutableComponent

    @WithStyle("gray_italic")
    @LangKey(text = ["Vibe cooking is my passion"])
    fun aiSlopTooltip(): MutableComponent

    @WithStyle("gray_italic")
    @LangKey(text = ["The buns are barely capable of holding it all together"])
    fun ultradenseMetalBallBurgerTooltip(): MutableComponent

    @LangKey(text = ["Sneak + Right-click"], key = "sneak_rc_activate_1")
    fun sneakRCActivate1(): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["%s to activate"], key = "sneak_rc_activate_2")
    fun sneakRCActivate2(@WithStyle("highlight") key: Component): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["Enabled: %s"])
    fun enabledPrefix(state: Component): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["%s | %s"])
    fun enabledPrefixAlt(
        @WithStyle("highlight") name: Component,
        state: Component
    ): MutableComponent

    @WithStyle("gray")
    @LangKey(text = ["None"])
    fun none(): MutableComponent

}
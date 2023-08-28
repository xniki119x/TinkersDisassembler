package f1cont.niki119.tinkersdisassemble.mixins;

import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInput;
//import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;

@Mixin(IncrementalModifierRecipe.class)
public class MixinIncrementalModifierRecipe implements IRecipeWithInput {
    @Final
    @Shadow(remap = false)
    private Ingredient input;
    @Final
    @Shadow(remap = false)
    private int amountPerInput;
    @Final
    @Shadow(remap = false)
    private int neededPerLevel;
    public Ingredient getInput(){
        return input;
    }
    public int getAmountPerInput(){
        return amountPerInput;
    }
    public int getNeededPerLevel(){
        return neededPerLevel;
    }
}

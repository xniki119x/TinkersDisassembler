package f1cont.niki119.tinkersdisassemble.mixins;

import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInput;
//import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;

@Mixin(OverslimeModifierRecipe.class)
public class MixinOverslimeModifierRecipe implements IRecipeWithInput {
    @Final
    @Shadow(remap = false)
    private Ingredient ingredient;
    @Override
    public Ingredient getInput() {
        return ingredient;
    }

    @Override
    public int getAmountPerInput() {
        return 0;
    }

    @Override
    public int getNeededPerLevel() {
        return 0;
    }
}

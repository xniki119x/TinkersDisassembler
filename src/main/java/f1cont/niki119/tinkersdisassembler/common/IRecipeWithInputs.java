package f1cont.niki119.tinkersdisassembler.common;

//import net.minecraft.item.crafting.Ingredient;
//import slimeknights.mantle.recipe.SizedIngredient;

import slimeknights.mantle.recipe.ingredient.SizedIngredient;

import java.util.List;

public interface IRecipeWithInputs {
    List<SizedIngredient> getInputs();
}

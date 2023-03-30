package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.item.crafting.Ingredient;
import slimeknights.mantle.recipe.SizedIngredient;

import java.util.List;

public interface IRecipeWithInputs {
    List<SizedIngredient> getInputs();
}

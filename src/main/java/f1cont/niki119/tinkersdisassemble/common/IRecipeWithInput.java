package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.item.crafting.Ingredient;

public interface IRecipeWithInput {
    Ingredient getInput();
    int getAmountPerInput();
    int getNeededPerLevel();
}

package f1cont.niki119.tinkersdisassembler.common.registry;

import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;

public class RegistryCrafts {
    public static void register()
    {
        registerRecipes("disassembler");
    }

    private static void registerRecipes(String name)
    {
        CraftingHelper.register(TinkersDisassembler.prefix(name), (IRecipeFactory) (context, json) -> CraftingHelper.getRecipe(json, context));
    }
}

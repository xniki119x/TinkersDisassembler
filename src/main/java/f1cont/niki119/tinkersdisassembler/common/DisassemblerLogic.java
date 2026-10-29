package f1cont.niki119.tinkersdisassembler.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

/**
 * Чистая логика разбора тинкеровских инструментов: без GUI и без изменения инструмента.
 */
public final class DisassemblerLogic {

    private DisassemblerLogic() {
    }

    /** Стоимость разбора в уровнях опыта: 1 за каждую деталь + 1 за каждый уровень модификатора. */
    public static int calculateCost(Level level, ItemStack stack) {
        if (!(stack.getItem() instanceof IModifiableDisplay)) {
            return 0;
        }
        ToolStack tool = ToolStack.from(stack);
        int parts = tool.getMaterials().size();
        int modifierLevels = tool.getUpgrades().getModifiers().stream()
                .mapToInt(ModifierEntry::getLevel)
                .sum();
        return parts + modifierLevels;
    }

    /** Полный список предметов, возвращаемых при разборе: детали с материалами + ингредиенты модификаторов. */
    public static List<ItemStack> computeResult(Level level, ItemStack stack) {
        List<ItemStack> result = new ArrayList<>();
        if (!(stack.getItem() instanceof IModifiableDisplay)) {
            return result;
        }
        // ToolStack пишет NBT прямо в переданный стак, поэтому работаем с копией:
        // предпросмотр на клиенте и расчёт на сервере не должны менять настоящий инструмент
        ToolStack tool = ToolStack.from(stack.copy());
        RecipeManager recipeManager = level.getRecipeManager();
        List<ITinkerStationRecipe> stationRecipes = recipeManager.getAllRecipesFor(TinkerRecipeTypes.TINKER_STATION.get());

        for (ModifierEntry entry : tool.getUpgrades().getModifiers()) {
            Modifier modifier = entry.getModifier();
            List<AbstractModifierRecipe> matching = stationRecipes.stream()
                    .filter(AbstractModifierRecipe.class::isInstance)
                    .map(AbstractModifierRecipe.class::cast)
                    .filter(recipe -> recipe.getDisplayResult().getModifier().equals(modifier))
                    .collect(Collectors.toList());
            if (matching.isEmpty()) {
                continue;
            }
            AbstractModifierRecipe recipe = matching.get(0);
            if (recipe instanceof IncrementalModifierRecipe) {
                IRecipeWithInput withInput = (IRecipeWithInput) recipe;
                Ingredient input = withInput.getInput();
                int perInput = withInput.getAmountPerInput();
                if (perInput <= 0 || input.isEmpty()) {
                    continue;
                }
                int levels = entry.getLevel();
                int progress = ((IncrementalModifier) modifier).getAmount(tool);
                // текущий прогресс + полный возврат за все снятые уровни (как в исходном цикле)
                int items = progress / perInput + (levels - 1) * (withInput.getNeededPerLevel() / perInput);
                if (items > 0) {
                    result.add(withCount(input.getItems()[0], items));
                }
            } else if (recipe instanceof ModifierRecipe) {
                for (SizedIngredient ingredient : ((IRecipeWithInputs) recipe).getInputs()) {
                    List<ItemStack> variants = ingredient.getMatchingStacks();
                    if (variants.isEmpty()) {
                        continue;
                    }
                    int index = recipe.toString().contains("recapitated") ? 11 : 0;
                    if (index >= variants.size()) {
                        index = 0;
                    }
                    result.add(withCount(variants.get(index), ingredient.getAmountNeeded()));
                }
            }
            // OverslimeModifierRecipe не наследует AbstractModifierRecipe, поэтому ищем его отдельно
            if (modifier instanceof OverslimeModifier overslime) {
                List<OverslimeModifierRecipe> overslimeRecipes = stationRecipes.stream()
                        .filter(OverslimeModifierRecipe.class::isInstance)
                        .map(OverslimeModifierRecipe.class::cast)
                        .collect(Collectors.toList());
                if (!overslimeRecipes.isEmpty()) {
                    Ingredient input = ((IRecipeWithInput) overslimeRecipes.get(0)).getInput();
                    int amount = overslime.getOverslime(tool);
                    if (amount > 0 && !input.isEmpty()) {
                        result.add(withCount(input.getItems()[0], amount / 10));
                    }
                }
            }
        }

        List<PartRequirement> parts = ((IModifiable) stack.getItem()).getToolDefinition().getData().getParts();
        MaterialNBT materials = tool.getMaterials();
        int count = Math.min(parts.size(), materials.size());
        for (int i = 0; i < count; i++) {
            IToolPart part = parts.get(i).getPart();
            if (part.asItem() instanceof ToolPartItem) {
                MaterialVariant material = materials.get(i);
                result.add(((ToolPartItem) part.asItem()).withMaterial(material.getVariant()));
            }
        }
        return mergeStacks(result);
    }

    /** Разобрать инструмент: выдать предметы и списать опыт. Возвращает стоимость или -1, если опыта не хватает. */
    public static int disassemble(Player player, Level level, ItemStack tool) {
        if (!(tool.getItem() instanceof IModifiableDisplay)) {
            return -1;
        }
        int cost = calculateCost(level, tool);
        if (player.experienceLevel < cost) {
            return -1;
        }
        for (ItemStack stack : computeResult(level, tool)) {
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
        }
        player.giveExperienceLevels(-cost);
        return cost;
    }

    private static ItemStack withCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    /** Складывает одинаковые предметы в стакы по 64, чтобы предпросмотр занимал меньше ячеек. */
    private static List<ItemStack> mergeStacks(List<ItemStack> stacks) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            int remaining = stack.getCount();
            for (ItemStack target : merged) {
                if (remaining <= 0) {
                    break;
                }
                if (target.getCount() < 64 && ItemStack.isSame(target, stack) && ItemStack.tagMatches(target, stack)) {
                    int move = Math.min(64 - target.getCount(), remaining);
                    target.grow(move);
                    remaining -= move;
                }
            }
            while (remaining > 0) {
                int portion = Math.min(64, remaining);
                ItemStack copy = stack.copy();
                copy.setCount(portion);
                merged.add(copy);
                remaining -= portion;
            }
        }
        return merged;
    }
}

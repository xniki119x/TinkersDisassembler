package f1cont.niki119.tinkersdisassemble.common.blocks;

import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInputs;
import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInput;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.*;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRequirements;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import java.util.*;
import java.util.stream.Collectors;

public class DisassemblerBlock extends BaseBlock implements IWaterLoggable {
    protected static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape TABLE_SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D),  // top
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 15.0D, 4.0D),     // leg
            Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 15.0D, 4.0D),   // leg
            Block.makeCuboidShape(12.0D, 0.0D, 12.0D, 16.0D, 15.0D, 16.0D), // leg
            Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 4.0D, 15.0D, 16.0D)).simplify();

    public DisassemblerBlock() {
        super("disassembler", AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.5F).sound(SoundType.WOOD));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    public void disassembleTool(World world, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        if (stack.getItem() instanceof ToolItem) {
            ToolStack toolStack = ToolStack.from(stack);
            for (ModifierEntry entry : toolStack.getUpgrades().getModifiers()) {
                Modifier modifier = entry.getModifier();
                RecipeManager rm = world.getRecipeManager();
                List<ITinkerStationRecipe> recipes = rm.getRecipesForType(RecipeTypes.TINKER_STATION);
                List<AbstractModifierRecipe> modifierRecipes = recipes
                        .stream()
                        .filter(input -> input instanceof AbstractModifierRecipe)
                        .map(p -> (AbstractModifierRecipe) p)
                        .collect(Collectors.toList());
                List<AbstractModifierRecipe> recipesForModifier = modifierRecipes.stream().filter(i -> i.getDisplayResult().getModifier().equals(modifier)).collect(Collectors.toList());
                if (!recipesForModifier.isEmpty()) {
                    AbstractModifierRecipe recipe1 = recipesForModifier.get(0);

                    if (recipe1 instanceof IncrementalModifierRecipe) {
                        TinkersDisassemble.LOGGER.info("IncrementalModifierRecipe");
                        List<IncrementalModifierRecipe> incrementalModifierRecipes = recipes
                                .stream()
                                .filter(input -> input instanceof IncrementalModifierRecipe)
                                .map(p -> (IncrementalModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<IncrementalModifierRecipe> lll = incrementalModifierRecipes.stream().filter(i -> i.getDisplayResult().getModifier().equals(modifier)).collect(Collectors.toList());
                        IncrementalModifierRecipe recipe = lll.get(0);
                        IRecipeWithInput ir = ((IRecipeWithInput) recipe);
                        Ingredient input = ir.getInput();
                        int level = entry.getLevel();
                        int perInput = ir.getAmountPerInput();
                        int neededPerLevel = ir.getNeededPerLevel();
                        int amount = ((IncrementalModifier) modifier).getAmount(toolStack);
                        while (level >= 1) {
                            while (amount >= perInput) {
                                amount -= perInput;
                                ItemStack stack1 = input.getMatchingStacks()[0].copy();
                                player.addItemStackToInventory(stack1);
                            }
                            toolStack.removeModifier(modifier, 1);
                            level--;
                            amount = neededPerLevel;
                        }
                    } else if (recipe1 instanceof ModifierRecipe) {
                        TinkersDisassemble.LOGGER.info("ModifierRecipe");
                        List<ModifierRecipe> modifierRecipes1 = recipes
                                .stream()
                                .filter(input -> input instanceof ModifierRecipe)
                                .map(p -> (ModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<ModifierRecipe> lll = modifierRecipes1.stream().filter(i -> i.getDisplayResult().getModifier().equals(modifier)).collect(Collectors.toList());
                        ModifierRecipe recipe = lll.get(0);
                        IRecipeWithInputs rwi = (IRecipeWithInputs) recipe;
                        List<SizedIngredient> lsi = rwi.getInputs();
                        for (SizedIngredient si : lsi) {
                            int amount = si.getAmountNeeded();
                            ItemStack stack1 = si.getMatchingStacks().get(recipe.toString().contains("recapitated")?11:0);
                            ItemStack stack2 = stack1.copy();
                            stack2.setCount(amount);
                            player.addItemStackToInventory(stack2);
                        }
                        toolStack.removeModifier(modifier, 1);
                    } else {
                        TinkersDisassemble.LOGGER.info("Other");
                    }
                } else {
                    if (modifier instanceof OverslimeModifier) {
                        OverslimeModifier om = (OverslimeModifier) modifier;
                        List<OverslimeModifierRecipe> modifierRecipes1 = recipes
                                .stream()
                                .filter(input -> input instanceof OverslimeModifierRecipe)
                                .map(p -> (OverslimeModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<OverslimeModifierRecipe> recipesForModifier1 = modifierRecipes1.stream().filter(i -> i.getDisplayResult().getModifier().equals(modifier)).collect(Collectors.toList());
                        if (!recipesForModifier1.isEmpty()) {
                            int cap = om.getOverslime(toolStack);
                            OverslimeModifierRecipe recipe = recipesForModifier1.get(1);
                            IRecipeWithInput ir = ((IRecipeWithInput) recipe);
                            Ingredient input = ir.getInput();
                            ItemStack stack1 = input.getMatchingStacks()[0].copy();
                            stack1.setCount(cap / 10);
                            player.addItemStackToInventory(stack1);
                            toolStack.removeModifier(modifier, 1);
                        }
                    }
                }
            }
            List<PartRequirement> components = ((IModifiable) stack.getItem()).getToolDefinition().getData().getParts();
            if (!components.isEmpty()) {

                List<IMaterial> materials = toolStack.getMaterialsList();
                if (!materials.isEmpty()) {
                    for (int i = 0; i < materials.size(); ++i) {
                        PartRequirement requirement = components.get(i);
                        IToolPart part = requirement.getPart();
                        IMaterial material = materials.get(i);
                        ToolPartItem partItem = (ToolPartItem) part.asItem();
                        player.addItemStackToInventory(partItem.withMaterial(material));
                    }
                }
                player.setHeldItem(handIn, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            disassembleTool(worldIn, player, handIn);
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return TABLE_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, flag);
    }

    @Deprecated
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Deprecated
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Deprecated
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }
}

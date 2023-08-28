package f1cont.niki119.tinkersdisassemble.common.blocks;

import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInputs;
import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import f1cont.niki119.tinkersdisassemble.common.IRecipeWithInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.*;
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

import java.util.*;
import java.util.stream.Collectors;

public class DisassemblerBlock extends BaseBlock implements SimpleWaterloggedBlock
{
    protected static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape TABLE_SHAPE = Shapes.or(Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0), Block.box(0.0, 0.0, 0.0, 4.0, 15.0, 4.0), Block.box(12.0, 0.0, 0.0, 16.0, 15.0, 4.0), Block.box(12.0, 0.0, 12.0, 16.0, 15.0, 16.0), Block.box(0.0, 0.0, 12.0, 4.0, 15.0, 16.0)).optimize();

    public DisassemblerBlock()
    {
        super("disassembler", BlockBehaviour.Properties.of(Material.WOOD)
                .strength(0.5F)
                .sound(SoundType.WOOD));
        this.registerDefaultState(this.getStateDefinition().any()
                 .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
                                 InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide)
        {
            disassembleTool(worldIn, player, handIn);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TABLE_SHAPE;
    }
    //
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
    }
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, WATERLOGGED});
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public void disassembleTool(Level world, Player player, InteractionHand handIn)
    {
        ItemStack stack = player.getItemInHand(handIn);
        System.out.println(stack.getItem().getClass());
        if (stack.getItem() instanceof IModifiableDisplay)
        {
            ToolStack toolStack = ToolStack.from(stack);
            for (ModifierEntry entry : toolStack.getUpgrades().getModifiers())
            {
                Modifier modifier = entry.getModifier();
                RecipeManager rm = world.getRecipeManager();
                List<ITinkerStationRecipe> recipes = rm.getAllRecipesFor(TinkerRecipeTypes.TINKER_STATION.get());
                List<AbstractModifierRecipe> modifierRecipes = recipes.stream()
                        .filter(input -> input instanceof AbstractModifierRecipe)
                        .map(p -> (AbstractModifierRecipe) p)
                        .collect(Collectors.toList());
                List<AbstractModifierRecipe> recipesForModifier = modifierRecipes.stream()
                        .filter(i -> i.getDisplayResult()
                                .getModifier()
                                .equals(modifier))
                        .collect(Collectors.toList());
                if (!recipesForModifier.isEmpty())
                {
                    AbstractModifierRecipe recipe1 = recipesForModifier.get(0);

                    if (recipe1 instanceof IncrementalModifierRecipe)
                    {
                        TinkersDisassemble.LOGGER.info("IncrementalModifierRecipe");
                        List<IncrementalModifierRecipe> incrementalModifierRecipes = recipes.stream()
                                .filter(input -> input instanceof IncrementalModifierRecipe)
                                .map(p -> (IncrementalModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<IncrementalModifierRecipe> lll = incrementalModifierRecipes.stream()
                                .filter(i -> i.getDisplayResult()
                                        .getModifier()
                                        .equals(modifier))
                                .collect(Collectors.toList());
                        IncrementalModifierRecipe recipe = lll.get(0);
                        IRecipeWithInput ir = ((IRecipeWithInput) recipe);
                        Ingredient input = ir.getInput();
                        int level = entry.getLevel();
                        int perInput = ir.getAmountPerInput();
                        int neededPerLevel = ir.getNeededPerLevel();
                        int amount = ((IncrementalModifier) modifier).getAmount(toolStack);
                        while (level >= 1)
                        {
                            while (amount >= perInput)
                            {
                                amount -= perInput;
                                ItemStack stack1 = input.getItems()[0].copy();
                                if (!player.addItem(stack1))
                                {
                                    player.drop(stack1, false);
                                }
                            }
                            toolStack.removeModifier(modifier.getId(), 1);
                            level--;
                            amount = neededPerLevel;
                        }
                    }
                    else if (recipe1 instanceof ModifierRecipe)
                    {
                        TinkersDisassemble.LOGGER.info("ModifierRecipe");
                        List<ModifierRecipe> modifierRecipes1 = recipes.stream()
                                .filter(input -> input instanceof ModifierRecipe)
                                .map(p -> (ModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<ModifierRecipe> lll = modifierRecipes1.stream()
                                .filter(i -> i.getDisplayResult()
                                        .getModifier()
                                        .equals(modifier))
                                .collect(Collectors.toList());
                        ModifierRecipe recipe = lll.get(0);
                        IRecipeWithInputs rwi = (IRecipeWithInputs) recipe;
                        List<SizedIngredient> lsi = rwi.getInputs();
                        for (SizedIngredient si : lsi)
                        {
                            int amount = si.getAmountNeeded();
                            ItemStack stack1 = si.getMatchingStacks()
                                    .get(recipe.toString()
                                            .contains("recapitated") ? 11 : 0);
                            ItemStack stack2 = stack1.copy();
                            stack2.setCount(amount);
                            if (!player.addItem(stack2))
                            {
                                player.drop(stack2, false);
                            }
                        }
                        toolStack.removeModifier(modifier.getId(), 1);
                    }
                    else
                    {
                        TinkersDisassemble.LOGGER.info("Other");
                    }
                }
                else
                {
                    if (modifier instanceof OverslimeModifier)
                    {
                        OverslimeModifier om = (OverslimeModifier) modifier;
                        List<OverslimeModifierRecipe> modifierRecipes1 = recipes.stream()
                                .filter(input -> input instanceof OverslimeModifierRecipe)
                                .map(p -> (OverslimeModifierRecipe) p)
                                .collect(Collectors.toList());
                        List<OverslimeModifierRecipe> recipesForModifier1 = modifierRecipes1.stream()
                                .filter(i -> i.getDisplayResult()
                                        .getModifier()
                                        .equals(modifier))
                                .collect(Collectors.toList());
                        if (!recipesForModifier1.isEmpty())
                        {
                            int cap = om.getOverslime(toolStack);
                            OverslimeModifierRecipe recipe = recipesForModifier1.get(1);
                            IRecipeWithInput ir = ((IRecipeWithInput) recipe);
                            Ingredient input = ir.getInput();
                            ItemStack stack1 = input.getItems()[0].copy();
                            stack1.setCount(cap / 10);
                            if (!player.addItem(stack1))
                            {
                                player.drop(stack1, false);
                            }
                            toolStack.removeModifier(modifier.getId(), 1);
                        }
                    }
                }
          }
            List<PartRequirement> components = ((IModifiable) stack.getItem()).getToolDefinition()
                    .getData()
                    .getParts();
            if (!components.isEmpty())
            {

                MaterialNBT materials = toolStack.getMaterials();
                if (materials.size()>0)
                {
                    for (int i = 0; i < materials.size(); ++i)
                    {
                        PartRequirement requirement = components.get(i);
                        IToolPart part = requirement.getPart();
                        MaterialVariant material = materials.get(i);
                        ToolPartItem partItem = (ToolPartItem) part.asItem();
                        if (!player.addItem(partItem.withMaterial(material.getVariant())))
                        {
                            player.drop(partItem.withMaterial(material.getVariant()), false);
                        }
                    }
                }
                player.setItemInHand(handIn, ItemStack.EMPTY);
            }
        }
    }

}

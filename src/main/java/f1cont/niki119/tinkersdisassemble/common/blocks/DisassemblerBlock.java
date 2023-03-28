package f1cont.niki119.tinkersdisassemble.common.blocks;

import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRequirements;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.TinkerToolParts;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    public void disassemble(){

    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote) {
            ItemStack stack = player.getHeldItem(handIn);
            if (stack.getItem() instanceof ToolItem) {
                ToolStack toolStack = ToolStack.from(stack);
                List<PartRequirement> components = ((IModifiable)stack.getItem()).getToolDefinition().getData().getParts();
//                if(!components.isEmpty()) {
//                    player.setHeldItem(handIn, ItemStack.EMPTY);
//                    List<IMaterial> materials = toolStack.getMaterialsList();
//                    if (!materials.isEmpty()) {
//                        for(int i = 0; i < materials.size(); ++i) {
//                            PartRequirement requirement = components.get(i);
//                            IToolPart part = requirement.getPart();
//                            IMaterial material = materials.get(i);
//                            ToolPartItem partItem = (ToolPartItem) part.asItem();
//                            player.addItemStackToInventory(partItem.withMaterial(material));
//                        }
//                    }
//                }

                for (ModifierEntry entry : toolStack.getUpgrades().getModifiers()) {
                        Modifier modifier = entry.getModifier();
                            int level = entry.getLevel();
                    //ForgeRegistries.RECIPE_SERIALIZERS.get
                    //RegistryManager.ACTIVE.getRegistry(new ResourceLocation())
                            TinkersDisassemble.LOGGER.info(modifier.getDisplayName(toolStack, level));
                            TinkersDisassemble.LOGGER.info(modifier.getDescription(toolStack, level));
                            //modifier.onRemoved(toolStack);
                }
            }
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

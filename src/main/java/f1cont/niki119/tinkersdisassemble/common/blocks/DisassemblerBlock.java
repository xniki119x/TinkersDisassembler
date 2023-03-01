package f1cont.niki119.tinkersdisassemble.common.blocks;

import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
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

public class DisassemblerBlock extends BaseBlock{
    public DisassemblerBlock() {
        super("disassembler", AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.5F).sound(SoundType.WOOD));
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
                if(!components.isEmpty()) {
                    player.setHeldItem(handIn, ItemStack.EMPTY);
                    List<IMaterial> materials = toolStack.getMaterialsList();
                    if (!materials.isEmpty()) {
                        for(int i = 0; i < materials.size(); ++i) {
                            PartRequirement requirement = components.get(i);
                            IToolPart part = requirement.getPart();
                            IMaterial material = materials.get(i);
                            ToolPartItem partItem = (ToolPartItem) part.asItem();
                            player.addItemStackToInventory(partItem.withMaterial(material));
                        }
                    }
                }

                //for (ModifierEntry entry : toolStack.getUpgrades().getModifiers()) {
                //        Modifier modifier = entry.getModifier();
                //            int level = entry.getLevel();
//
                //            TinkersDisassemble.LOGGER.info(modifier.getDisplayName(toolStack, level));
                //            TinkersDisassemble.LOGGER.info(modifier.getDescription(toolStack, level));
                //            modifier.onRemoved(toolStack);
                //}
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}

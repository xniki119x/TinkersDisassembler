package f1cont.niki119.tinkersdisassembler.common.blocks;

import c4.conarm.lib.armor.ArmorPart;
import c4.conarm.lib.tinkering.TinkersArmor;
import c4.conarm.lib.utils.RecipeMatchHolder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import slimeknights.mantle.property.PropertyUnlistedDirection;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;

import javax.annotation.Nonnull;
import java.util.*;

public class DisassemblerBlock extends BaseBlock {
    public static final PropertyUnlistedDirection FACING = new PropertyUnlistedDirection("facing",EnumFacing.Plane.HORIZONTAL);
    private static ImmutableList<AxisAlignedBB> BOUNDS_Table = ImmutableList.of(new AxisAlignedBB(0.0, 0.75, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.25, 0.75, 0.25), new AxisAlignedBB(0.75, 0.0, 0.0, 1.0, 0.75, 0.25), new AxisAlignedBB(0.75, 0.0, 0.75, 1.0, 0.75, 1.0), new AxisAlignedBB(0.0, 0.0, 0.75, 0.25, 0.75, 1.0));
    public DisassemblerBlock() {
        super("disassembler");
        setCreativeTab(CreativeTabs.MISC);
    }
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{FACING});
    }
    public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        return raytraceMultiAABB(BOUNDS_Table, pos, start, end);
    }
    public static RayTraceResult raytraceMultiAABB(List<AxisAlignedBB> aabbs, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> list = Lists.newArrayList();
        Iterator var5 = aabbs.iterator();

        while(var5.hasNext()) {
            AxisAlignedBB axisalignedbb = (AxisAlignedBB)var5.next();
            list.add(rayTrace2(pos, start, end, axisalignedbb));
        }

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0;
        Iterator var8 = list.iterator();

        while(var8.hasNext()) {
            RayTraceResult raytraceresult = (RayTraceResult)var8.next();
            if (raytraceresult != null) {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);
                if (d0 > d1) {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }
    private static RayTraceResult rayTrace2(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        Vec3d vec3d1 = end.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), raytraceresult.sideHit, pos);
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    public void disassembleTool(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        // Проверка, что предмет из тинкера.
        if (stack.getItem() instanceof TinkersItem)
        {
            TinkersItem tinkersItem = (TinkersItem) stack.getItem();
            // Получение частей инструмента
            List<PartMaterialType> partMaterialTypes = tinkersItem.getToolBuildComponents();
            // Получение материалов для частей инструмента.
            NBTTagCompound nbtStack = TagUtil.getTagSafe(stack);
            NBTTagCompound nbtTinkerData = nbtStack.getCompoundTag("TinkerData");
            NBTTagList materialList = nbtTinkerData.getTagList("Materials",8);
            // Разборка на части
            for (int i = 0; i < partMaterialTypes.size(); i++)
            {
                PartMaterialType partMaterialType = partMaterialTypes.get(i);
                List<IToolPart> parts = new ArrayList<>(partMaterialType.getPossibleParts());
                ToolPart part = (ToolPart)parts.get(0);
                Material material = TinkerRegistry.getMaterial(materialList.getStringTagAt(i));
                // Выдача части инструмента игроку
                player.addItemStackToInventory(part.getItemstackWithMaterial(material));
            }
            // Модификаторы и ресурсы из них...
            NBTTagList modifiers = nbtTinkerData.getTagList("Modifiers",8);
            NBTTagList tagListModifiers = nbtStack.getTagList("Modifiers", 10);
            for(int i = 0; i < modifiers.tagCount(); i++)
            {
                String nameModifier = modifiers.getStringTagAt(i);
                for(int j = 0; j < tagListModifiers.tagCount(); j++)
                {
                    NBTTagCompound nbtModifier = tagListModifiers.getCompoundTagAt(j);
                    String identifier = nbtModifier.getString("identifier");
                    boolean f = false;
                    for(int k = 0; k < modifiers.tagCount(); k++)
                    {
                        if(modifiers.getStringTagAt(k).equals(identifier))
                        {
                            f = true;
                            tagListModifiers.removeTag(j);
                            break;
                        }
                    }
                    if(f)
                    {
                        int current = nbtModifier.getInteger("current"); if(current==0) current = 1;
                        int level = nbtModifier.getInteger("level");
                        IModifier iModifier = TinkerRegistry.getModifier(nameModifier);
                        if(iModifier instanceof Modifier)
                        {
                            List<RecipeMatch> recipeMatchList = getRecipes((Modifier) iModifier);
                            int rmli = 0;
                            RecipeMatch recipeMatch = recipeMatchList.get(rmli++);
                            if(recipeMatch instanceof RecipeMatch.ItemCombination)
                            {
                                RecipeMatch.ItemCombination itemCombination = (RecipeMatch.ItemCombination) recipeMatch;
                                List<ItemStack> inputs = itemCombination.getInputs();
                                for(ItemStack stack1 : inputs)
                                {
                                    player.addItemStackToInventory(new ItemStack(stack1.getItem(),level));
                                }
                                break;
                            }else if(recipeMatch instanceof RecipeMatch.Oredict) {
                                RecipeMatch.Oredict oredict = (RecipeMatch.Oredict) recipeMatch;
                                ItemStack input = oredict.getInputs().get(0);
                                while (current>0) {
                                    if(current>=oredict.amountMatched) {
                                        player.addItemStackToInventory(new ItemStack(input.getItem(),oredict.amountNeeded));
                                        current -= oredict.amountMatched;
                                    }else {
                                        recipeMatch = recipeMatchList.get(rmli++);
                                        oredict = (RecipeMatch.Oredict) recipeMatch;
                                        input = oredict.getInputs().get(0);
                                    }
                                }
                                break;
                            }else {
                                ItemStack input = recipeMatch.getInputs().get(0);
                                player.addItemStackToInventory(new ItemStack(input.getItem(),recipeMatch.amountNeeded));
                                break;
                            }
                        }
                    }
                }
            }
            // Удаление предмета из руки
            player.setHeldItem(hand, new ItemStack(Items.AIR));
            // Проверка что предмет из Construct Armory
        } else if(TinkersDisassembler.constructArmoryLoaded)
        {
            if(stack.getItem() instanceof TinkersArmor)
            {
                TinkersArmor tinkersArmor = (TinkersArmor) stack.getItem();
                // Получение частей инструмента
                List<PartMaterialType> partMaterialTypes = tinkersArmor.getArmorBuildComponents();
                // Получение материалов для частей инструмента.
                NBTTagCompound nbtStack = TagUtil.getTagSafe(stack);
                NBTTagCompound nbtTinkerData = nbtStack.getCompoundTag("TinkerData");
                NBTTagList materialList = nbtTinkerData.getTagList("Materials",8);
                // Разборка на части
                for (int i = 0; i < partMaterialTypes.size(); i++)
                {
                    PartMaterialType partMaterialType = partMaterialTypes.get(i);
                    List<IToolPart> parts = new ArrayList<>(partMaterialType.getPossibleParts());
                    ArmorPart part = (ArmorPart)parts.get(0);
                    Material material = TinkerRegistry.getMaterial(materialList.getStringTagAt(i));
                    // Выдача части инструмента игроку
                    player.addItemStackToInventory(part.getItemstackWithMaterial(material));
                }

                // Модификаторы и ресурсы из них...
                NBTTagList modifiers = nbtTinkerData.getTagList("Modifiers",8);
                NBTTagList tagListModifiers = nbtStack.getTagList("Modifiers", 10);
                for(int i = 0; i < modifiers.tagCount(); i++)
                {
                    String nameModifier = modifiers.getStringTagAt(i);
                    for(int j = 0; j < tagListModifiers.tagCount(); j++)
                    {
                        NBTTagCompound nbtModifier = tagListModifiers.getCompoundTagAt(j);
                        String identifier = nbtModifier.getString("identifier");
                        boolean f = false;
                        for(int k = 0; k < modifiers.tagCount(); k++)
                        {
                            if(modifiers.getStringTagAt(k).equals(identifier))
                            {
                                f = true;
                                tagListModifiers.removeTag(j);
                                break;
                            }
                        }
                        if(f)
                        {
                            int current = nbtModifier.getInteger("current"); if(current==0) current = 1;
                            int level = nbtModifier.getInteger("level");
                            IModifier iModifier = TinkerRegistry.getModifier(nameModifier);
                            if(iModifier instanceof Modifier)
                            {
                                List<RecipeMatch> recipeMatchList = getRecipesArmor((Modifier) iModifier);
                                int rmli = 0;
                                RecipeMatch recipeMatch = recipeMatchList.get(rmli++);
                                if(recipeMatch instanceof RecipeMatch.ItemCombination)
                                {
                                    RecipeMatch.ItemCombination itemCombination = (RecipeMatch.ItemCombination) recipeMatch;
                                    List<ItemStack> inputs = itemCombination.getInputs();
                                    for(ItemStack stack1 : inputs)
                                    {
                                        player.addItemStackToInventory(new ItemStack(stack1.getItem(),level));
                                    }
                                    break;
                                }else if(recipeMatch instanceof RecipeMatch.Oredict) {
                                    RecipeMatch.Oredict oredict = (RecipeMatch.Oredict) recipeMatch;
                                    ItemStack input = oredict.getInputs().get(0);
                                    while (current>0) {
                                        if(current>=oredict.amountMatched) {
                                            player.addItemStackToInventory(new ItemStack(input.getItem(),oredict.amountNeeded));
                                            current -= oredict.amountMatched;
                                        }else {
                                            recipeMatch = recipeMatchList.get(rmli++);
                                            oredict = (RecipeMatch.Oredict) recipeMatch;
                                            input = oredict.getInputs().get(0);
                                        }
                                    }
                                    break;
                                }else {
                                    ItemStack input = recipeMatch.getInputs().get(0);
                                    player.addItemStackToInventory(new ItemStack(input.getItem(),recipeMatch.amountNeeded));
                                    break;
                                }
                            }
                        }
                    }
                }
                // Удаление предмета из руки
                player.setHeldItem(hand, new ItemStack(Items.AIR));
            }
        }
    }
    private List<RecipeMatch> getRecipes(Modifier modifier)
    {
        Iterator var2 = modifier.items.iterator();
        List<RecipeMatch> recipeMatchList = new ArrayList<>();
        while(var2.hasNext()) {
            RecipeMatch recipe = (RecipeMatch)var2.next();
            recipeMatchList.add(recipe);
        }
        return recipeMatchList;
    }
    private List<RecipeMatch> getRecipesArmor(Modifier modifier)
    {
        List<RecipeMatch> recipeMatchList = new ArrayList<>();
        Optional<PriorityQueue<RecipeMatch>> recipes = RecipeMatchHolder.getRecipes(modifier);
        if (recipes.isPresent()) {
            PriorityQueue<RecipeMatch> recipeMatches = (PriorityQueue)recipes.get();
            Iterator var4 = recipeMatches.iterator();

            while(var4.hasNext()) {
                RecipeMatch rm = (RecipeMatch)var4.next();
                List<ItemStack> in = rm.getInputs();
                if (!in.isEmpty()) {
                    recipeMatchList.add(rm);
                }
            }
        }

        return recipeMatchList;
    }
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            disassembleTool(worldIn, player, hand);
        }
        return false;
    }
}

package com.github.theredbrain.mergeditems.block;

import com.github.theredbrain.mergeditems.block.entity.ItemMergingBlockEntity;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemMergingBlock extends BlockWithEntity {

	public static final MapCodec<ItemMergingBlock> CODEC = createCodec(ItemMergingBlock::new);
	private static final DirectionProperty FACING;
	private static final BooleanProperty WATERLOGGED;
	private static final VoxelShape BASE_SHAPE;
	private static final VoxelShape X_STEP_SHAPE;
	private static final VoxelShape X_STEM_SHAPE;
	private static final VoxelShape X_FACE_SHAPE;
	private static final VoxelShape Z_STEP_SHAPE;
	private static final VoxelShape Z_STEM_SHAPE;
	private static final VoxelShape Z_FACE_SHAPE;
	private static final VoxelShape X_AXIS_SHAPE;
	private static final VoxelShape Z_AXIS_SHAPE;

	public ItemMergingBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<ItemMergingBlock> getCodec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ItemMergingBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise())
				.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ItemMergingBlockEntity itemMergingBlockEntity) {
			player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
				@Override
				public ItemMergingScreenHandler.ItemMergingData getScreenOpeningData(ServerPlayerEntity player) {
					return new ItemMergingScreenHandler.ItemMergingData(itemMergingBlockEntity.getMaxMergedItemsAmount(), itemMergingBlockEntity.getList());
				}

				@Override
				public Text getDisplayName() {
					return Text.translatable(itemMergingBlockEntity.getTitle());
				}

				@Nullable
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
					return new ItemMergingScreenHandler(syncId, playerInventory, itemMergingBlockEntity.getMaxMergedItemsAmount(), itemMergingBlockEntity.getList());
				}
			});
		}
		return ActionResult.CONSUME;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = (Direction)state.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	static {
		FACING = HorizontalFacingBlock.FACING;
		WATERLOGGED = Properties.WATERLOGGED;
		BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
		X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
		X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
		X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
		Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
		Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
		Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
		X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, new VoxelShape[]{X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE});
		Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, new VoxelShape[]{Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE});
	}
}

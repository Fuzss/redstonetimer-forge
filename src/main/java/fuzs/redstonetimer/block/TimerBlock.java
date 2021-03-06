package fuzs.redstonetimer.block;

import fuzs.redstonetimer.RedstoneTimer;
import fuzs.redstonetimer.block.entity.TimerBlockEntity;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Random;

public class TimerBlock extends DiodeBlock implements EntityBlock {
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public TimerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(LOCKED, false));
    }

    @Override
    protected int getDelay(BlockState state) {
        return 2;
    }

    @Override
    public boolean isLocked(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return pLevel instanceof Level level && this.getInputSignal(level, pPos, pState) > 0;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
        super.tick(pState, pLevel, pPos, pRand);
        RedstoneTimer.LOGGER.info("Powered: {}", pState.getValue(POWERED));
    }

    @Override
    protected boolean shouldTurnOn(Level pLevel, BlockPos pPos, BlockState pState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        final boolean b = blockEntity instanceof TimerBlockEntity timer && timer.isPowered();
        RedstoneTimer.LOGGER.info("Should turn on: {}", b);
        return b;
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (!pBlockState.getValue(POWERED)) {
            return 0;
        } else {
            return pSide.getAxis().getPlane() == Direction.Plane.HORIZONTAL && pBlockState.getValue(FACING).getOpposite() != pSide ? this.getOutputSignal(pBlockAccess, pPos, pBlockState) : 0;
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (!state.getValue(LOCKED)) {
            double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            double y = (double)pos.getY() + 0.9D + (random.nextDouble() - 0.5D) * 0.2D;
            double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = super.getStateForPlacement(pContext);
        return blockstate.setValue(LOCKED, Boolean.valueOf(this.isLocked(pContext.getLevel(), pContext.getClickedPos(), blockstate)));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return !pLevel.isClientSide() && pFacing == pState.getValue(FACING) ? pState.setValue(LOCKED, Boolean.valueOf(this.isLocked(pLevel, pCurrentPos, pState))) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), world.isClientSide ? TimerBlockEntity::clientTick : TimerBlockEntity::serverTick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TimerBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, LOCKED);
    }

    @Override
    protected void updateNeighborsInFront(Level pLevel, BlockPos pPos, BlockState pState) {
        Direction facing = pState.getValue(FACING);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == facing) continue;
            BlockPos blockpos = pPos.relative(direction);
            if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(pLevel, pPos, pLevel.getBlockState(pPos), java.util.EnumSet.of(direction.getOpposite()), false).isCanceled())
                continue;
            pLevel.neighborChanged(blockpos, this, pPos);
            pLevel.updateNeighborsAtExceptFromFacing(blockpos, this, direction.getOpposite());
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pPlayer.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (!pLevel.isClientSide) {
            this.openScreen(pLevel, pPos, pPlayer);
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    private void openScreen(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof TimerBlockEntity) {
            pPlayer.openMenu((TimerBlockEntity)blockentity);
        }
    }
}

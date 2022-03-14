package fuzs.redstonetimer.world.level.block;

import fuzs.redstonetimer.registry.ModRegistry;
import fuzs.redstonetimer.world.level.block.entity.TimerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.Random;

public class TimerBlock extends DiodeBlock implements EntityBlock {
    public static final BooleanProperty PULSE = BooleanProperty.create("pulse");

    public TimerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(PULSE, false));
    }

    @Override
    protected int getDelay(BlockState state) {
        return TimerBlockEntity.PULSE_LENGTH;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!this.isLocked(level, pos, state)) {
            boolean flag = state.getValue(POWERED);
            boolean flag1 = this.shouldTurnOn(level, pos, state);
            if (flag && !flag1) {
                level.setBlock(pos, state.setValue(POWERED, Boolean.FALSE).setValue(PULSE, Boolean.FALSE), 2);
            } else if (!flag) {
                boolean flag2 = super.shouldTurnOn(level, pos, state);
                level.setBlock(pos, state.setValue(POWERED, Boolean.TRUE).setValue(PULSE, !flag2), 2);
                if (!flag1) {
                    level.scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
                }
            }
        }
    }

    @Override
    protected boolean shouldTurnOn(Level pLevel, BlockPos pPos, BlockState pState) {
        if (super.shouldTurnOn(pLevel, pPos, pState)) return true;
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        return blockEntity instanceof TimerBlockEntity timer && timer.isPowered();
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
        if (!state.getValue(POWERED) || state.getValue(PULSE)) {
            double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            double y = (double)pos.getY() + 0.9D + (random.nextDouble() - 0.5D) * 0.2D;
            double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

//    @Override
//    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
//        return !pLevel.isClientSide() && pFacing == pState.getValue(FACING) ? pState.setValue(PULSE, this.isLocked(pLevel, pCurrentPos, pState)) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
//    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), world.isClientSide ? TimerBlockEntity::clientTick : TimerBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
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
        builder.add(FACING, POWERED, PULSE);
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
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof TimerBlockEntity timerBlockEntity) {
                pPlayer.openMenu(timerBlockEntity);
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
}

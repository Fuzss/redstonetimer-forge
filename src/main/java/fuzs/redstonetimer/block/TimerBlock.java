package fuzs.redstonetimer.block;

import fuzs.redstonetimer.block.entity.TimerBlockEntity;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
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

    protected int getOutputSignal(BlockGetter world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof TimerBlockEntity && ((TimerBlockEntity)blockEntity).isPowered() ? 15 : 0;
    }

    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (!state.getValue(POWERED)) {
            double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            double y = (double)pos.getY() + 0.9D + (random.nextDouble() - 0.5D) * 0.2D;
            double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), TimerBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TimerBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, LOCKED);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
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

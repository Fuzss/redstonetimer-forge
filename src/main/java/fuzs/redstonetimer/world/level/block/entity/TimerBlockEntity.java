package fuzs.redstonetimer.world.level.block.entity;

import fuzs.redstonetimer.registry.ModRegistry;
import fuzs.redstonetimer.world.inventory.TimerMenu;
import fuzs.redstonetimer.world.level.block.TimerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TimerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MIN_INTERVAL = 4;
    public static final int MAX_INTERVAL = 72000;

    private int time;
    private int interval = 40;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? TimerBlockEntity.this.interval : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                TimerBlockEntity.this.setInterval(value);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public TimerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.time = nbt.getInt("Time");
        this.interval = nbt.getInt("Interval");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Time", this.time);
        nbt.putInt("Interval", this.interval);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int getTicksActive() {
        return 2;
    }

    public boolean isPowered() {
        return this.time >= this.interval - this.getTicksActive();
    }

    public boolean hasPowerStateChanged() {
        return this.time == 0 || this.time == this.interval - this.getTicksActive();
    }

    private void setInterval(int interval) {
        int oldInterval = this.interval;
        this.interval = Mth.clamp(interval, MIN_INTERVAL, MAX_INTERVAL);
        if (this.interval != oldInterval) {
            this.time = 0;
            if (this.level != null) {
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    public float getRotationAngle(float tickDelta) {
        float rotationAngle = 180.0F;
        if (!this.isPowered()) {
            float rotationTime = this.time - this.getTicksActive() + tickDelta;
            rotationAngle += rotationTime / (this.interval - this.getTicksActive()) * -360.0F;
        }
        return rotationAngle % 360.0F;
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new TimerMenu(pContainerId, this, this.dataAccess);
    }

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, TimerBlockEntity pBlockEntity) {
        tick(pLevel, pPos, pState, pBlockEntity);
    }

    public static void serverTick(Level level, BlockPos pPos, BlockState pState, TimerBlockEntity pBlockEntity) {
        boolean unlocked = tick(level, pPos, pState, pBlockEntity);
        if (unlocked && pBlockEntity.hasPowerStateChanged() && pState.getBlock() instanceof TimerBlock timerBlock) {
            timerBlock.tick(pState, (ServerLevel) level, pPos, level.random);
        }
    }

    private static boolean tick(Level world, BlockPos pos, BlockState state, TimerBlockEntity blockEntity) {
        if (state.getValue(TimerBlock.LOCKED)) {
            blockEntity.time = 0;
            return false;
        }
        if (++blockEntity.time >= blockEntity.interval) {
            blockEntity.time = 0;
        }
        if (blockEntity.hasPowerStateChanged() && blockEntity.isPowered()) {
            world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.5F);
        }
        return true;
    }

    @Override
    public Component getDisplayName() {
        return TextComponent.EMPTY;
    }
}

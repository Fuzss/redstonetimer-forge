package fuzs.redstonetimer.block.entity;

import fuzs.redstonetimer.registry.ModRegistry;
import fuzs.redstonetimer.world.inventory.TimerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TimerBlockEntity extends BlockEntity implements MenuProvider {
    private int time;
    private int interval = 40;

    public TimerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public CompoundTag save(CompoundTag nbt) {
        super.save(nbt);
        nbt.putInt("Time", this.time);
        nbt.putInt("Interval", this.interval);
        return nbt;
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.time = nbt.getInt("Time");
        this.interval = nbt.getInt("Interval");
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public int getTimeActive() {
        return 2;
    }

    public boolean isPowered() {
        return this.time < this.getTimeActive();
    }

    public void setInterval(int interval) {
        this.interval = interval;
        this.time = 0;
    }

    private boolean isRotating() {
        return !this.isPowered();
    }

    public float getRotationAngle(float tickDelta) {
        float rotationAngle = 0.0F;
        if (this.isRotating()) {
            float rotationTime = this.time - this.getTimeActive() + tickDelta;
            rotationAngle = rotationTime / (this.interval - this.getTimeActive()) * -360.0F;
        }
        return rotationAngle;
    }

    public int getMinInterval() {
        return 4;
    }

    public int getMaxInterval() {
        return 72000;
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
        return new TimerMenu(pContainerId, this, this.interval);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, TimerBlockEntity blockEntity) {
        if (++blockEntity.time >= blockEntity.interval) {
            blockEntity.time = 0;
            world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.5F);
        }
    }

    @Override
    public Component getDisplayName() {
        return TextComponent.EMPTY;
    }
}

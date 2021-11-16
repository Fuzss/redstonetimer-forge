package fuzs.redstonetimer.world.inventory;

import fuzs.redstonetimer.block.entity.TimerBlockEntity;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;

public class TimerMenu extends AbstractContainerMenu {
    private final TimerBlockEntity timer;
    private final DataSlot interval = DataSlot.standalone();

    public TimerMenu(int pContainerId) {
        this(pContainerId, null, 0);
    }

    public TimerMenu(int pContainerId, TimerBlockEntity timer, int interval) {
        super(ModRegistry.TIMER_MENU_TYPE.get(), pContainerId);
        this.timer = timer;
        this.addDataSlot(this.interval).set(interval);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.timer.stillValid(pPlayer);
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pPlayer.mayBuild()) {
            int interval = this.interval.get() + this.getTimerIncrement(pId);
            int clampedInterval = Mth.clamp(interval, 4, 72000);
            if (interval == clampedInterval) {
                if (this.timer != null) {
                    this.timer.setInterval(interval);
                    this.broadcastChanges();
                }
                return true;
            }
        }
        return false;
    }

    public String getInterval() {
        return String.format("%.3f", this.interval.get() / 20.0F);
    }

    private int getTimerIncrement(int buttonId) {
        return (int) Math.signum(buttonId) * switch (Math.abs(buttonId)) {
            // in game ticks
            case 1 -> 2;
            case 2 -> 20;
            case 3 -> 200;
            case 4 -> 1200;
            default -> 0;
        };
    }
}

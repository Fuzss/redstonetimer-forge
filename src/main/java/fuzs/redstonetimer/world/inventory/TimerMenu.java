package fuzs.redstonetimer.world.inventory;

import fuzs.redstonetimer.block.entity.TimerBlockEntity;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class TimerMenu extends AbstractContainerMenu {
    private final TimerBlockEntity timer;
    private final ContainerData timerData;

    public TimerMenu(int pContainerId) {
        this(pContainerId, null, new SimpleContainerData(1));
    }

    public TimerMenu(int pContainerId, TimerBlockEntity timer, ContainerData timerData) {
        super(ModRegistry.TIMER_MENU_TYPE.get(), pContainerId);
        this.timer = timer;
        this.timerData = timerData;
        this.addDataSlots(timerData);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.timer.stillValid(pPlayer);
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pPlayer.mayBuild()) {
            int interval = this.timerData.get(0) + this.getTicksIncrement(pId);
            interval = Mth.clamp(interval, TimerBlockEntity.MIN_INTERVAL, TimerBlockEntity.MAX_INTERVAL);
            if (interval != this.timerData.get(0)) {
                this.setData(0, interval);
                return true;
            }
        }
        return false;
    }

    public void setData(int pId, int pData) {
        super.setData(pId, pData);
        this.broadcastChanges();
    }

    public int getTicksInterval() {
        return this.timerData.get(0);
    }

    private int getTicksIncrement(int buttonId) {
        return (int) Math.signum(buttonId) * switch (Math.abs(buttonId)) {
            // in game ticks
            case 1 -> 4;
            case 2 -> 20;
            case 3 -> 200;
            case 4 -> 1200;
            default -> 0;
        };
    }
}

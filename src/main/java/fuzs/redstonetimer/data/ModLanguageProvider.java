package fuzs.redstonetimer.data;

import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(DataGenerator gen, String modid) {
        super(gen, modid, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.TIMER_BLOCK.get(), "Timer");
        this.add("gui.timer.interval", "Timer Intervall: %ss");
        this.add("gui.timer.minus.minute", "-%sm");
        this.add("gui.timer.minus.second", "-%ss");
        this.add("gui.timer.minus.milli", "-%sms");
        this.add("gui.timer.plus.minute", "+%sm");
        this.add("gui.timer.plus.second", "+%ss");
        this.add("gui.timer.plus.milli", "+%sms");
    }
}

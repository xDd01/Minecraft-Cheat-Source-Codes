/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.combat;

import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.timer.Stopwatch;
import java.util.Arrays;

public class ClickRandomizer {
    private final Stopwatch stopwatch = new Stopwatch();
    private final NumberProperty minimum;
    private final NumberProperty maximum;
    private final NumberProperty randomizationLevel;
    private final BooleanProperty useRandomization;
    private boolean addTime;
    private int targetDropStreak;
    private int completedDropStreak;

    public ClickRandomizer(NumberProperty min, NumberProperty max, NumberProperty randomizationLevel, BooleanProperty useRandomization) {
        this.minimum = min;
        this.maximum = max;
        this.randomizationLevel = randomizationLevel;
        this.useRandomization = useRandomization;
    }

    public boolean hasElapsed() {
        boolean click;
        double min = ((Number)this.minimum.getValue()).doubleValue();
        double max = ((Number)this.maximum.getValue()).doubleValue();
        if (!((Boolean)this.useRandomization.getValue()).booleanValue()) {
            boolean click2;
            long cps = (long)Math.max(min, max);
            boolean bl2 = click2 = cps > 0L && this.stopwatch.hasElapsed(1000L / cps);
            if (click2) {
                this.stopwatch.reset();
            }
            return click2;
        }
        int level = ((Number)this.randomizationLevel.getValue()).intValue();
        double[] randomValues = new double[level];
        int i2 = 0;
        while (i2 < level) {
            randomValues[i2] = RandomUtil.random(min, max);
            int n2 = i2++;
            randomValues[n2] = randomValues[n2] + RandomUtil.random(-0.5 * (double)level, 0.5 * (double)level);
        }
        double cps = Arrays.stream(randomValues).sum() / (double)level;
        if (this.addTime && this.targetDropStreak > this.completedDropStreak && cps > 1.0) {
            cps /= 1.25 + RandomUtil.random(0.1, 0.4);
            ++this.completedDropStreak;
        }
        if (click = this.stopwatch.hasElapsed((long)(1000.0 / cps))) {
            this.stopwatch.reset();
            if (RandomUtil.random(0, 30) == 5) {
                this.addTime = true;
                this.targetDropStreak = RandomUtil.random(2, 5);
                this.completedDropStreak = 0;
            }
        }
        return click;
    }
}


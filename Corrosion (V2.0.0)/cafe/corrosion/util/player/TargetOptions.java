/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

public class TargetOptions {
    private final boolean hostile;
    private final boolean animals;
    private final boolean players;
    private final boolean invisible;
    private final boolean hitbox;
    private final double range;

    protected TargetOptions(TargetOptionsBuilder<?, ?> b2) {
        this.hostile = ((TargetOptionsBuilder)b2).hostile;
        this.animals = ((TargetOptionsBuilder)b2).animals;
        this.players = ((TargetOptionsBuilder)b2).players;
        this.invisible = ((TargetOptionsBuilder)b2).invisible;
        this.hitbox = ((TargetOptionsBuilder)b2).hitbox;
        this.range = ((TargetOptionsBuilder)b2).range;
    }

    public static TargetOptionsBuilder<?, ?> builder() {
        return new TargetOptionsBuilderImpl();
    }

    public boolean isHostile() {
        return this.hostile;
    }

    public boolean isAnimals() {
        return this.animals;
    }

    public boolean isPlayers() {
        return this.players;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean isHitbox() {
        return this.hitbox;
    }

    public double getRange() {
        return this.range;
    }

    public TargetOptions(boolean hostile, boolean animals, boolean players, boolean invisible, boolean hitbox, double range) {
        this.hostile = hostile;
        this.animals = animals;
        this.players = players;
        this.invisible = invisible;
        this.hitbox = hitbox;
        this.range = range;
    }

    private static final class TargetOptionsBuilderImpl
    extends TargetOptionsBuilder<TargetOptions, TargetOptionsBuilderImpl> {
        private TargetOptionsBuilderImpl() {
        }

        @Override
        protected TargetOptionsBuilderImpl self() {
            return this;
        }

        @Override
        public TargetOptions build() {
            return new TargetOptions(this);
        }
    }

    public static abstract class TargetOptionsBuilder<C extends TargetOptions, B extends TargetOptionsBuilder<C, B>> {
        private boolean hostile;
        private boolean animals;
        private boolean players;
        private boolean invisible;
        private boolean hitbox;
        private double range;

        protected abstract B self();

        public abstract C build();

        public B hostile(boolean hostile) {
            this.hostile = hostile;
            return this.self();
        }

        public B animals(boolean animals) {
            this.animals = animals;
            return this.self();
        }

        public B players(boolean players) {
            this.players = players;
            return this.self();
        }

        public B invisible(boolean invisible) {
            this.invisible = invisible;
            return this.self();
        }

        public B hitbox(boolean hitbox) {
            this.hitbox = hitbox;
            return this.self();
        }

        public B range(double range) {
            this.range = range;
            return this.self();
        }

        public String toString() {
            return "TargetOptions.TargetOptionsBuilder(hostile=" + this.hostile + ", animals=" + this.animals + ", players=" + this.players + ", invisible=" + this.invisible + ", hitbox=" + this.hitbox + ", range=" + this.range + ")";
        }
    }
}


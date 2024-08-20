package com.quackimpala.quimps.acc;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public interface PoweredRailMixinAccessor {
    EnumProperty<PowerDirection> MOMENTUM = EnumProperty.of("momentum", PowerDirection.class,
            PowerDirection.NEUTRAL,
            PowerDirection.NEGATIVE,
            PowerDirection.POSITIVE
    );

    enum PowerDirection implements StringIdentifiable {
        NEUTRAL("neutral", 0),
        NEGATIVE("negative", -1),
        POSITIVE("positive", 1);

        private final String name;
        private final int toInt;

        PowerDirection(final String name, final int toInt) {
            this.name = name;
            this.toInt = toInt;
        }

        public String getName() {
            return name;
        }

        public int toInt() {
            return toInt;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}

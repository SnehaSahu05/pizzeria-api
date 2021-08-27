package com.ss.pizzeria.backend.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Interface for Enumerations of the Pizza specifications
 *
 * @author Sneha
 */
public interface Pizza {

    @Getter
    @AllArgsConstructor
    enum Crust {
        THIN("Thin");
        private String text;
    }

    @Getter
    @AllArgsConstructor
    enum Flavour {
        HAWAII("Hawaii"), REGINA("Regina"), QUARTTRO_FORMAGGI("Quattro-Formaggi");
        private String text;
    }
    @Getter
    @AllArgsConstructor
    enum Size {
        L("Large"), M("Medium");
        private String text;
    }
}

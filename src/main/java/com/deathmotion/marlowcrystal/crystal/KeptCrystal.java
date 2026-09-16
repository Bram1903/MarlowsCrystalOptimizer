package com.deathmotion.marlowcrystal.crystal;

public interface KeptCrystal {

    void marlowcrystal$keep(long keptAt, int sequence);

    void marlowcrystal$release();

    boolean marlowcrystal$isKept();

    long marlowcrystal$keptAt();

    int marlowcrystal$sequence();
}

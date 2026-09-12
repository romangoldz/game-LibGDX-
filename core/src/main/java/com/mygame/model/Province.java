package com.mygame.model;

public class Province {
    public int id;
    public String name;
    public String ownerTag;     // тег страны-владельца
    public int centroidX, centroidY; // для подписи (опционально)

    public Province(int id, String name, String ownerTag) {
        this.id = id;
        this.name = name;
        this.ownerTag = ownerTag;
    }
}
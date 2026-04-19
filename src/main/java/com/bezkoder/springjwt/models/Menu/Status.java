package com.bezkoder.springjwt.models.Menu;

public enum Status {
    NONE("Очікує оплату","#000000"),
    PAYED("Оплачений",  "#211d00"),
    DONE("Виданий", "#0f2300");

    private final String title;
    private final String color;
    Status(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }
}

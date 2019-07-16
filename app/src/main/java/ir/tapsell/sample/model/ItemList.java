package ir.tapsell.sample.model;

import java.io.Serializable;

import ir.tapsell.sample.enums.ListItemType;

public class ItemList implements Serializable {
    public ListItemType listItemType;
    public String id;
    public String title;
}

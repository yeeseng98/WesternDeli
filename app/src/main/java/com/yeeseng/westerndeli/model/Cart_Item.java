package com.yeeseng.westerndeli.model;

public class Cart_Item {

    private String ItemName;
    private Integer ItemCost;
    private Integer ItemPrepTime;
    private Integer PortionCount;

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public Integer getItemCost() {
        return ItemCost;
    }

    public void setItemCost(Integer itemCost) {
        ItemCost = itemCost;
    }

    public Integer getItemPrepTime() {
        return ItemPrepTime;
    }

    public void setItemPrepTime(Integer itemPrepTime) {
        ItemPrepTime = itemPrepTime;
    }

    public Integer getPortionCount() {
        return PortionCount;
    }

    public void setPortionCount(Integer portionCount) {
        PortionCount = portionCount;
    }
}

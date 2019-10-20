package com.yeeseng.westerndeli.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Order_Item implements Parcelable {

    private String ItemName;
    private Integer ItemCost;
    private Integer ItemPrepTime;
    private String ItemCategory;
    private Integer Portion;

    public Order_Item(){}
    
    public Integer getPortion() {
        return Portion;
    }

    public void setPortion(Integer portion) {
        Portion = portion;
    }

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

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemName);
        if (ItemCost == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ItemCost);
        }
        if (ItemPrepTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ItemPrepTime);
        }
        dest.writeString(ItemCategory);
        if (Portion == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(Portion);
        }
    }

    protected Order_Item(Parcel in) {
        ItemName = in.readString();
        ItemCost = in.readByte() == 0x00 ? null : in.readInt();
        ItemPrepTime = in.readByte() == 0x00 ? null : in.readInt();
        ItemCategory = in.readString();
        Portion = in.readByte() == 0x00 ? null : in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Order_Item> CREATOR = new Parcelable.Creator<Order_Item>() {
        @Override
        public Order_Item createFromParcel(Parcel in) {
            return new Order_Item(in);
        }

        @Override
        public Order_Item[] newArray(int size) {
            return new Order_Item[size];
        }
    };
}

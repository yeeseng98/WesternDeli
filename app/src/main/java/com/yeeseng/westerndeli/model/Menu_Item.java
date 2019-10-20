package com.yeeseng.westerndeli.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;

public class Menu_Item implements Parcelable {

    private String ItemName;
    private String ItemUrl;
    private String ItemDescription;
    private Integer ItemCost;
    private Boolean ChefRecommended;
    private Integer ItemPrepTime;
    private String ItemCategory;
    private String ItemFilterBy;

    public Menu_Item(){ }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemUrl() {
        return ItemUrl;
    }

    public void setItemUrl(String itemUrl) {
        ItemUrl = itemUrl;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public Integer getItemCost() {
        return ItemCost;
    }

    public void setItemCost(Integer itemCost) {
        ItemCost = itemCost;
    }

    public Boolean getChefRecommended() {
        return ChefRecommended;
    }

    public void setChefRecommended(Boolean chefRecommended) {
        ChefRecommended = chefRecommended;
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

    public String getItemFilterBy() {
        return ItemFilterBy;
    }

    public void setItemFilterBy(String itemFilterBy) {
        ItemFilterBy = itemFilterBy;
    }

    protected Menu_Item(Parcel in) {
        ItemName = in.readString();
        ItemUrl = in.readString();
        ItemDescription = in.readString();
        ItemCost = in.readByte() == 0x00 ? null : in.readInt();
        byte ChefRecommendedVal = in.readByte();
        ChefRecommended = ChefRecommendedVal == 0x02 ? null : ChefRecommendedVal != 0x00;
        ItemPrepTime = in.readByte() == 0x00 ? null : in.readInt();
        ItemCategory = in.readString();
        ItemFilterBy = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemName);
        dest.writeString(ItemUrl);
        dest.writeString(ItemDescription);
        if (ItemCost == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ItemCost);
        }
        if (ChefRecommended == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (ChefRecommended ? 0x01 : 0x00));
        }
        if (ItemPrepTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ItemPrepTime);
        }
        dest.writeString(ItemCategory);
        dest.writeString(ItemFilterBy);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Menu_Item> CREATOR = new Parcelable.Creator<Menu_Item>() {
        @Override
        public Menu_Item createFromParcel(Parcel in) {
            return new Menu_Item(in);
        }

        @Override
        public Menu_Item[] newArray(int size) {
            return new Menu_Item[size];
        }
    };
}

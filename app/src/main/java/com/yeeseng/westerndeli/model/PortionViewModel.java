package com.yeeseng.westerndeli.model;

import android.arch.lifecycle.ViewModel;

public class PortionViewModel extends ViewModel {

    public Integer portion;

    public Integer getPortion() {
        return portion;
    }

    public void setPortion(Integer portion) {
        this.portion = portion;
    }
}

package com.authcoinandroid.model.contract;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ContractResponse {

    @Expose
    private List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
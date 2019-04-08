package com.pertamina.brightgas.util;

import java.util.ArrayList;

public class ItemOrderConfiguration {

    public int qty = 0;

    public ArrayList<Integer> listCheckbox = new ArrayList<>();

    public ArrayList<Integer> listQty = new ArrayList<>();

    int checked = -1;

    public boolean isChecked() {
        if (checked == -1)
            return false;
        return true;
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            checked = 1;
        } else {
            checked = -1;
        }
    }

    public void setChecked(int index, int qty) {
        Integer toBeCheck = new Integer(index);
        if (qty > 0) {
            if (listCheckbox.size() == 0) {
                listCheckbox.add(index);
                listQty.add(qty);
            } else {
                boolean alreadyThere = false;
                for (Integer i : listCheckbox) {
                    if (i.intValue() == index) {
                        alreadyThere = true;
                        break;
                    }
                }
                if (!alreadyThere) {
                    listCheckbox.add(index);
                    listQty.add(qty);
                }
            }
        } else {
            listCheckbox.remove(toBeCheck);
            listQty.remove(toBeCheck);
        }

        if (listCheckbox.size() > 0) {
            checked = 1;
        } else {
            checked = -1;
        }

    }

    public boolean isValid() {
        if (listCheckbox.size() > 0) {
            return true;
        } else {
            if (qty > 0) {
                return true;
            }
            return false;
        }
    }

}

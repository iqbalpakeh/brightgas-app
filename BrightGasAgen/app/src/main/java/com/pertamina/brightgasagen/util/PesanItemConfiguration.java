package com.pertamina.brightgasagen.util;

import java.util.ArrayList;

/**
 * Created by gumelartejasukma on 11/12/16.
 */
public class PesanItemConfiguration {
    public int qty = 0;
    int checked = -1;
    public ArrayList<Integer> listCheckbox = new ArrayList<>();

    public boolean isChecked(){
        if(checked == -1)
            return false;
        return true;
    }

    public void setChecked(boolean isChecked){
        if(isChecked){
            checked = 1;
        }else{
            checked = -1;
        }
    }

    public void setChecked(boolean isCheked, int index){
        Integer toBeCheck = new Integer(index);
        if(isCheked){
            if(listCheckbox.size() == 0){
                listCheckbox.add(index);
            }else{
                boolean alreadyThere = false;
                for (Integer i:listCheckbox) {
                    if(i.intValue()==index){
                        alreadyThere = true;
                        break;
                    }
                }
                if(!alreadyThere){
                    listCheckbox.add(index);
                }
            }
        }else{
            listCheckbox.remove(toBeCheck);
        }

        if(listCheckbox.size()>0){
            checked = 1;
        }else{
            checked = -1;
        }

    }

    public boolean isValid(){
        if(qty>0 && isChecked()){
            return true;
        }
        return false;
    }

}

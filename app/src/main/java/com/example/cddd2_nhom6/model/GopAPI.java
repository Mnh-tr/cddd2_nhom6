package com.example.cddd2_nhom6.model;

import java.util.ArrayList;
import java.util.List;

public class GopAPI {
    private List<DSPhimAPI> dsPhimAPIList;

    public GopAPI() {
        this.dsPhimAPIList = new ArrayList<>();
    }
    public void addDSPhim(List<DSPhimAPI> listA, List<DSPhimAPI> listB) {
        if (listA != null) {
            dsPhimAPIList.addAll(listA);
        }
        if (listB != null) {
            dsPhimAPIList.addAll(listB);
        }
    }

    public List<DSPhimAPI> getDsPhimAPIList() {
        return dsPhimAPIList;
    }
}

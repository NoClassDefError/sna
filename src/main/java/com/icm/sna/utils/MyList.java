package com.icm.sna.utils;

import java.util.ArrayList;

class MyList<T> extends ArrayList<T> {
    public MyList(ArrayList<T>... a) {
        super();
        for (ArrayList<T> aa : a)
            this.addAll(aa);
    }
}

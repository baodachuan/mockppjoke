package com.bdc.ppjoker.model;

import java.util.List;

public class BottomBar {
    public int selectTab;
    public int inActiveSize;
    public int activeSize;
    public String activeColor;
    public String inActiveColor;
    public List<Tab> tabs;

    public class Tab {
        public String pageUrl;
        public int size;
        public int index;
        public String title;
        public boolean enable;
        public String tintColor;
    }
}

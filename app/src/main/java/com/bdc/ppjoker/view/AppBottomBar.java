package com.bdc.ppjoker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.bdc.ppjoker.R;
import com.bdc.ppjoker.model.BottomBar;
import com.bdc.ppjoker.util.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class AppBottomBar extends BottomNavigationView {

    private static int[] sIcons = new int[]{R.drawable.icon_tab_home,
            R.drawable.icon_tab_shafa,
            R.drawable.icon_tab_publish,
            R.drawable.icon_tab_find,
            R.drawable.icon_tab_mine};

    public AppBottomBar(Context context) {
        this(context, null);
    }

    public AppBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BottomBar bottomBar = AppConfig.getBottomBarConfig();

        int[][] state = new int[2][];
        state[0] = new int[]{android.R.attr.state_selected};
        state[1] = new int[]{};
        int[] color = new int[]{Color.parseColor(bottomBar.activeColor), Color.parseColor(bottomBar.inActiveColor)};
        ColorStateList stateList = new ColorStateList(state, color);
        setItemTextColor(stateList);
        setItemIconTintList(stateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        for (BottomBar.Tab tab : bottomBar.tabs) {
            if (!tab.enable)
                continue;
            int id = getId(tab.pageUrl);
            if (id <= 0)
                continue;
            MenuItem item = getMenu().add(0, id, tab.index, tab.title);
            item.setIcon(sIcons[tab.index]);
        }

        for (int i = 0; i < bottomBar.tabs.size(); i++) {
            BottomBar.Tab tab = bottomBar.tabs.get(i);
            int iconSize = dp2px(tab.size);
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setIconSize(iconSize);
            if (TextUtils.isEmpty(tab.title)) {
                itemView.setIconTintList(ColorStateList.valueOf(Color.parseColor(tab.tintColor)));
                itemView.setShifting(false);
            }
        }

        setSelectedItemId(bottomBar.selectTab);
    }

    private int dp2px(int dpvalue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpvalue + 0.5f);
    }

    private int getId(String pageUrl) {
        return AppConfig.getDestConfig().get(pageUrl).id;
    }
}

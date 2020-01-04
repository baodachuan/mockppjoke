package com.bdc.ppjoker.util;

import android.content.ComponentName;

import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.bdc.ppjoker.model.Destination;

import java.util.HashMap;
import java.util.Map;

public class NavGraphBuidler {
    public static void build(NavController controller){
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph graph=new NavGraph(new NavGraphNavigator(provider));

        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> destConfig=AppConfig.getDestConfig();
        for(Map.Entry<String,Destination> entry:destConfig.entrySet()){
            Destination selfDefineDestination=entry.getValue();
            if(selfDefineDestination.isFragment){
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setId(selfDefineDestination.id);
                destination.setClassName(selfDefineDestination.className);
                destination.addDeepLink(selfDefineDestination.pageUrl);

                graph.addDestination(destination);
            }else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(selfDefineDestination.id);
                destination.setComponentName(new ComponentName(AppGlobal.getApplication().getPackageName(),selfDefineDestination.className));
                destination.addDeepLink(selfDefineDestination.pageUrl);
                graph.addDestination(destination);
            }

            if(selfDefineDestination.asStarter){
                graph.setStartDestination(selfDefineDestination.id);
            }

        }

        controller.setGraph(graph);

    }
}

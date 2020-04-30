package kin.backupandrestore.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;

import androidx.constraintlayout.widget.Group;

public class ViewUtils {

    public static void registerToGroupOnClickListener(Group group, View root, View.OnClickListener listener) {
        int refIds[] = group.getReferencedIds();
        for (int id : refIds) {
            root.findViewById(id).setOnClickListener(listener);
        }
    }

    public static void setGroupEnable(Group group, View root, boolean enable) {
        int refIds[] = group.getReferencedIds();
        for (int id : refIds) {
            root.findViewById(id).setEnabled(enable);
        }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}

package com.abooc.test.data;

import java.util.ArrayList;

/**
 * Created by dayu on 2016/12/1.
 */
public class UserLoader {
    private static UserLoader ourInstance = new UserLoader();

    public static UserLoader getInstance() {
        return ourInstance;
    }

    static ArrayList<User> users = new ArrayList<User>();

    static {
        users.add(new User("fm-001", "张三", null));
        users.add(new User("fm-002", "李四", null));
        users.add(new User("fm-003", "王五", null));
    }

    private UserLoader() {
    }

    public static void loadUsers(OnGetUsersListener callback) {

        if (callback != null) {
            callback.onGet(users);
        }
    }


    public interface OnGetUsersListener {
        void onGet(ArrayList<User> users);
    }
}

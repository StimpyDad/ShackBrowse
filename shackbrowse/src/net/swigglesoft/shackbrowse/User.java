package net.swigglesoft.shackbrowse;

import java.util.Arrays;
import java.util.HashSet;

public class User {

    private final static HashSet<String> MODS = new HashSet<String>(Arrays.asList(new String[]{"axeman808", "daeadin", "dognose", "evildolemite", "hirez", "quix", "serpico74", "vincent grayson"}));
    private final static HashSet<String> EMPLOYEES = new HashSet<String>(Arrays.asList(new String[]{"the man with the briefcase", "sporkyreeve", "daniel_perez", "joshua hawkins", "brittany vincent", "beardedaxe", "gburke59", "plonkus", "hammersuit", "staymighty", "shugamom"}));

    public static Boolean isEmployee(String userName) {
        return EMPLOYEES.contains(userName.toLowerCase());
    }

    public static Boolean isModerator(String userName) {
        return MODS.contains(userName.toLowerCase());
    }

}

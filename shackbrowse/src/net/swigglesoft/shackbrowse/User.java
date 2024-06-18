package net.swigglesoft.shackbrowse;

import java.util.Arrays;
import java.util.HashSet;

public class User {

    private final static HashSet<String> MODS = new HashSet<String>(Arrays.asList(new String[] { "AxeMan808", "Daeadin", "dognose", "EvilDolemite", "hirez", "Morgin", "Quix", "redfive", "Serpico74", "sikander", "Vincent Grayson" }));
    private final static HashSet<String> EMPLOYEES = new HashSet<String>(Arrays.asList(new String[] { "the man with the briefcase", "SporkyReeve", "Daniel_Perez", "Joshua Hawkins", "Brittany Vincent", "beardedaxe", "GBurke59", "plonkus", "hammersuit", "staymighty", "shugamom" }));

    public static Boolean isEmployee(String userName)
    {
        return EMPLOYEES.contains(userName.toLowerCase());
    }

    public static Boolean isModerator(String userName)
    {
        return MODS.contains(userName.toLowerCase());
    }

}

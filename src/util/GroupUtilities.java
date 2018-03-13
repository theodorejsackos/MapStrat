package util;

import java.util.UUID;

public class GroupUtilities {

    public static long getRandomGroupId(){
        String id = UUID.randomUUID().toString();
        return groupIdFromName(id);
    }

    public static String groupNameFromId(long gid){
        StringBuilder sb = new StringBuilder(8);
        for(int i = 0; i < Long.BYTES; i++)
            sb.append((gid & (0xFF << (i << 3))) >> (i << 3));
        return sb.toString();
    }

    public static long groupIdFromName(String gname){
        long lid  = 0;
        for(int i = 0; i < Long.BYTES; i++)
            lid |= gname.charAt(i) << (i << 3);
        System.out.println(Long.toHexString(lid));
        return lid;
    }
}

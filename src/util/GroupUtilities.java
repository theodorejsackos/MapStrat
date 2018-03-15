package util;

import java.util.UUID;

public class GroupUtilities {

    public static long getRandomGroupId(){
        String id = UUID.randomUUID().toString();
        return groupIdFromName(id);
    }

    public static String groupNameFromId(long gid){
        char[] result = new char[8];
        for(int i = Long.BYTES - 1; i >= 0; i--) {
            long mask   = (0xFFL << (i * 8));
            long kernel = gid & mask;
            long LSB    = kernel >> (i * 8);
            result[i] = (char) LSB;
        }
        return new String(result);
    }

    public static long groupIdFromName(String gname){
        long gid  = 0;
        for(int i = 0; i < Long.BYTES; i++) {
            char c     = gname.charAt(i);
            long moved = ((long) c) << (i * 8);
            gid       |= moved;
        }
        return gid;
    }
}

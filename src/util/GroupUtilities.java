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

    /**
     *
     * @param inputGroup The string the user entered in the prompt to join a group. This string may or may not be valid
     * @return The valid group id string if it can be extracted from the inputGroup string, the empty string ("") if
     *         the inputGroup string was invalid. */
    public static String validateGroup(String inputGroup){
        /* Input group cannot be shorter than the required length of a group ID (8), or greater than the length of the
         * https address to website + length of group id (26). */
        if(inputGroup.length() < 8 || inputGroup.length() > 26)
            return "";


        if(inputGroup.length() == 8){
            for(char c : inputGroup.toCharArray()){
                if(!Character.isLetterOrDigit(c))
                    return "";
            }
            return inputGroup;
        }

        /* Longest first to avoid matching but not consuming everything */
        final String[] acceptable = {"https://mapgee.us/", "http://mapgee.us/", "mapgee.us/"};

        for(String prefix : acceptable) {
            if(inputGroup.startsWith(prefix)){
                return validateGroup(inputGroup.replace(prefix, "")); // Strip the prefix, check the group validity
            }
        }

        return "";
    }
}

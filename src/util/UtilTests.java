package util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static util.GroupUtilities.getRandomGroupId;
import static util.GroupUtilities.groupNameFromId;
import static util.GroupUtilities.groupIdFromName;

public class UtilTests {

    @Test
    public void testGroupUtil(){
        for(int i = 0; i < 100; i++){
            String gname = UUID.randomUUID().toString().substring(0, 8);
            Assert.assertEquals(gname, groupNameFromId(groupIdFromName(gname)));

            Random r = new Random();
            long gid = r.nextLong();
            Assert.assertEquals(gid, groupIdFromName(groupNameFromId(gid)));
        }
    }

    @Test
    public void testGroupValidation(){
        final String[] acceptable = {
                "asdfzxcv", "https://mapgee.us/asdfzxcv", "http://mapgee.us/asdfzxcv",
                "mapgee.us/asdfzxcv"
        };

        for(String input : acceptable)
            Assert.assertEquals(acceptable[0], GroupUtilities.validateGroup(input));

        final String[] unaccept = {
                "", "asd", "asdfzxcvb", "https://mapgee.us/asd", "https://mapgee.us/asdfzxcvb", "mapgee.us/asdfzxcvb"
        };

        for(String input : unaccept)
            Assert.assertEquals(unaccept[0], GroupUtilities.validateGroup(input));
    }
}

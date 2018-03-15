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
}

package version;

public class Version {

    /* Version Number must change with each commit that modifies the client/server interaction model. Such changes will
     * change the commit number (_._.XX) This includes changes that reflect bug fixes or minor improvements that users
     * will not notice.
     *
     * Larger functionality commits will change the minor version number (_.XX._)
     *
     * Major program overhaul (like the 1.0 release in this commit) will incriment the major version number (XX._._).
     */
    public static final String VERSION = "1.0.0";

    public static boolean validateVersion(String version){
        return VERSION.equals(version);
    }
}

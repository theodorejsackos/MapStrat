package version;

public class Version {

    /* Version Number Convention:
     * Major Version Number (XX._._):
     *   Must increment with each major project milestone, these changes ought to be significant enough for users to be able
     *   to clearly tell that the client or functionality has completely changed.
     *
     * Minor Version Number (_.XX._)
     *   Must change with each commit that modifies the client/server interaction model or adds new functionality or changes
     *   the format of existing functionality.
     *
     * Build Version Number (_._.XX)
     *   May increment periodically throughout the development process between major or minor version releases. This number
     *   is not checked for client/server compatibility. This number simply indicates the progress toward a minor version
     *   release and differentiates between different builds as they are migrated to the Azure host server for testing.
     *
     */
    public static final String VERSION = "1.1.1";

    public static boolean validateVersion(String chkVersion){
        String[] versionNums = chkVersion.split("\\.");
        String[] correctNums = VERSION.split("\\.");

        return versionNums[0].equals(correctNums[0]) && versionNums[1].equals((correctNums[1]));
    }
}

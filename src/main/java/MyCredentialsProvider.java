import yanwittmann.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyCredentialsProvider {

    static {
        initialize();
    }

    private static boolean initialized = false;

    public static String FTP_SERVER;
    public static String FTP_USER;
    public static String FTP_PASSWORD;

    private static void initialize() {
        try {
            List<String> loginData = FileUtils.readFileToArrayList(new File("res/logindata.private"));
            if (loginData.size() >= 1) FTP_SERVER = loginData.get(0);
            if (loginData.size() >= 2) FTP_USER = loginData.get(1);
            if (loginData.size() >= 3) FTP_PASSWORD = loginData.get(2);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

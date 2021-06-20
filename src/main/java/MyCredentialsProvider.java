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
    public static String FTP_SERVER_URL;
    public static String FTP_SERVER_PATH;

    private static void initialize() {
        try {
            List<String> loginData = FileUtils.readFileToArrayList(new File("res/logindata.private"));
            if (loginData.size() >= 1) FTP_SERVER = loginData.get(0);
            if (loginData.size() >= 2) FTP_USER = loginData.get(1);
            if (loginData.size() >= 3) FTP_PASSWORD = loginData.get(2);
            if (loginData.size() >= 4) FTP_SERVER_URL = loginData.get(3);
            if (loginData.size() >= 5) FTP_SERVER_PATH = loginData.get(4);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

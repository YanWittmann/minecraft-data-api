import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import yanwittmann.api.ApiTools;
import yanwittmann.file.File;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        extractBlocks();
        extractItems();
    }

    private final static String EXTRACT_ITEM_DATA_REGEX = "(?:<ul>)?<li><a href=\"([^\"]+)\"[^>]+><[^>]+><[^>]+background-position:([^\"]+)\"><[^>]+><[^>]+>(.*)</li>(?:</ul>)?";

    final static String CREATE_ITEMS = "create";
    final static String USE_ITEMS = "use";
    final static String INDIRECT_ITEMS = "indirect";
    final static String SPAWN_ITEMS = "spawn";
    final static String EDUCATION_ITEMS = "education";
    final static String UNIMPLEMENTED_ITEMS = "unimplemented";
    final static String REMOVED_ITEMS = "removed";

    public static void extractItems() throws IOException {
        List<String> overrideMinecraftIdentifiers = new File("res/override_minecraft_item_ids.txt").readToArrayList();
        BufferedImage allTextures = readImage(new File(API_TOOLS.get(new URL("https://static.wikia.nocookie.net/minecraft_gamepedia/images/f/f5/ItemCSS.png/revision/latest"), 864000000)));
        List<Item> items = new ArrayList<>();
        String currentItemType = "";
        System.out.println("Extracting items...");
        for (String s : new File(API_TOOLS.get(new URL("https://minecraft.fandom.com/wiki/Item"), 3600000)).readToArrayList()) {
            if (s.contains("Items that create blocks, fluids or entities")) {
                currentItemType = CREATE_ITEMS;
            } else if (s.contains("Items with use in the world")) {
                currentItemType = USE_ITEMS;
            } else if (s.contains("Items with indirect use in the world")) {
                currentItemType = INDIRECT_ITEMS;
            } else if (s.contains("Spawn eggs")) {
                currentItemType = SPAWN_ITEMS;
            } else if (s.contains("Education Edition only")) {
                currentItemType = EDUCATION_ITEMS;
            } else if (s.contains("Unimplemented items")) {
                currentItemType = UNIMPLEMENTED_ITEMS;
            } else if (s.contains("Removed items")) {
                currentItemType = REMOVED_ITEMS;
            }

            if (currentItemType.length() > 0 && s.matches(EXTRACT_ITEM_DATA_REGEX)) {
                String wikiPage = "https://minecraft.fandom.com" + s.replaceAll(EXTRACT_ITEM_DATA_REGEX, "$1");
                String imageLocation = s.replaceAll(EXTRACT_ITEM_DATA_REGEX, "$2");
                String itemName = s.replaceAll(EXTRACT_ITEM_DATA_REGEX, "$3").replaceAll("<[^>]+>", "")
                        .replace("&#8204;", "").replaceAll("\\[[^]]*]", "").replace("))", ")");

                String minecraftIdentifier = "minecraft:" + itemName.toLowerCase().replace(" ", "_").replace("'", "_")
                        .replace("&#8204;", "").replaceAll("\\[[^]]*]", "")
                        .replaceAll("music_disc_\\(([^)]*)\\)", "music_disc_$1").replace("raw_", "")
                        .replaceAll("banner_pattern_\\(([^)]*)\\)", "$1_banner_pattern")
                        .replaceAll("\\([^)]*\\)", "").replaceAll("_$", "");
                for (String overrideMinecraftIdentifier : overrideMinecraftIdentifiers) {
                    if (overrideMinecraftIdentifier.length() > 0 && !overrideMinecraftIdentifier.startsWith("#")) {
                        String[] split = overrideMinecraftIdentifier.split(" ");
                        if (split.length == 2 && split[0].equals(minecraftIdentifier)) {
                            minecraftIdentifier = split[1];
                            break;
                        }
                    }
                }

                items.add(new Item(currentItemType, itemName, minecraftIdentifier, wikiPage, new File(API_TOOLS.get(new URL(wikiPage), 864000000)), imageLocation));
            }
        }

        connectFTP();
        for (Item item : items) {
            item.grabDataFromWikiPage();
            System.out.println(item);
            item.extractImage(allTextures);
            item.uploadImage(ftpClient);
            String upload = item.upload();
            if (upload.contains("Error")) break;
        }
    }

    private static BufferedImage readImage(File file) throws IOException {
        BufferedImage in = ImageIO.read(file);
        BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        return newImage;
    }

    private static FTPClient ftpClient;

    private static void connectFTP() {
        if (ftpClient != null && ftpClient.isConnected()) return;
        try {
            System.out.println("Connecting to FTP server");
            ftpClient = new FTPClient();
            ftpClient.connect(MyCredentialsProvider.FTP_SERVER);
            ftpClient.enterLocalPassiveMode();
            ftpClient.login(MyCredentialsProvider.FTP_USER, MyCredentialsProvider.FTP_PASSWORD);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            System.out.println("Connected to FTP server: " + MyCredentialsProvider.FTP_SERVER);
        } catch (Exception e) {
            System.out.println("Unable to connect to FTP Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private final static String EXTRACT_BLOCK_DATA_REGEX = "(?:<ul>)?<li><[^>]+><img alt=\"[^\"]+\" src=\".+/scale-to-width[^\"]+\"[^>]+></a> (?:<i>)?<[^>]+href=\"([^\"]+)\"[^>]+>([^>]+)</a>.*</li>(?:</ul>)?";
    private final static Pattern EXTRACT_BLOCK_NOTES = Pattern.compile("<span title=\"([^\"]+)\"");
    private final static Pattern EXTRACT_BLOCK_IMAGES = Pattern.compile("(https://static.wikia.nocookie.net/minecraft_gamepedia/images/[^\"]+)/scale");
    private final static ApiTools API_TOOLS = new ApiTools(new File("download"));

    private final static String NORMAL_BLOCKS = "normal";
    private final static String TECHNICAL_BLOCKS = "technical";
    private final static String EDUCATION_BLOCKS = "education";
    private final static String REMOVED_BLOCKS = "removed";

    public static void extractBlocks() throws IOException {
        List<String> overrideMinecraftIdentifiers = new File("res/override_minecraft_block_ids.txt").readToArrayList();
        List<Block> blocks = new ArrayList<>();
        String currentBlockType = "";
        System.out.println("Extracting blocks...");
        for (String s : new File(API_TOOLS.get(new URL("https://minecraft.fandom.com/wiki/Block"), 3600000)).readToArrayList()) {
            if (s.contains("to show or hide the list")) {
                if (currentBlockType.length() == 0) currentBlockType = NORMAL_BLOCKS;
                else if (currentBlockType.equals(NORMAL_BLOCKS)) currentBlockType = TECHNICAL_BLOCKS;
            } else if (s.contains("when education options are enabled")) {
                currentBlockType = EDUCATION_BLOCKS;
            } else if (s.contains("Removed blocks no longer exist in current versions of the game")) {
                currentBlockType = REMOVED_BLOCKS;
            }
            if (currentBlockType.length() > 0 && s.matches(EXTRACT_BLOCK_DATA_REGEX)) {
                String wikiPage = "https://minecraft.fandom.com" + s.replaceAll(EXTRACT_BLOCK_DATA_REGEX, "$1");
                String itemName = s.replaceAll(EXTRACT_BLOCK_DATA_REGEX, "$2");

                List<String> notes = new ArrayList<>();
                Matcher notesMatcher = EXTRACT_BLOCK_NOTES.matcher(s);
                while (notesMatcher.find()) notes.add(notesMatcher.group(1).replace(".", ""));
                if (notes.size() > 0) {
                    if (notes.contains("This statement only applies to Java Edition")) {
                        for (int i = 0; i < notes.size(); i++) {
                            if (notes.get(i).equals("This statement applies only to upcoming versions of Minecraft"))
                                notes.set(i, "This statement applies only to upcoming versions of Minecraft Bedrock Edition");
                        }
                    } else if (notes.contains("This statement only applies to Bedrock Edition")) {
                        for (int i = 0; i < notes.size(); i++) {
                            if (notes.get(i).equals("This statement applies only to upcoming versions of Minecraft"))
                                notes.set(i, "This statement applies only to upcoming versions of Minecraft Java Edition");
                        }
                    }
                }

                List<String> images = new ArrayList<>();
                Matcher imagesMatcher = EXTRACT_BLOCK_IMAGES.matcher(s);
                while (imagesMatcher.find()) images.add(imagesMatcher.group(1));

                String minecraftIdentifier = "minecraft:" + itemName.toLowerCase().replace(" ", "_").replace("'", "_");
                if (minecraftIdentifier.matches("minecraft:block_of_.+"))
                    minecraftIdentifier = minecraftIdentifier.replaceAll("minecraft:block_of_(.+)", "minecraft:$1_block");
                for (String overrideMinecraftIdentifier : overrideMinecraftIdentifiers) {
                    if (overrideMinecraftIdentifier.length() > 0 && !overrideMinecraftIdentifier.startsWith("#")) {
                        String[] split = overrideMinecraftIdentifier.split(" ");
                        if (split.length == 2 && split[0].equals(minecraftIdentifier)) {
                            minecraftIdentifier = split[1];
                            break;
                        }
                    }
                }

                blocks.add(new Block(currentBlockType, itemName, minecraftIdentifier, wikiPage, images, notes, new File(API_TOOLS.get(new URL(wikiPage), 864000000))));
            }
        }

        for (Block block : blocks) {
            block.grabDataFromWikiPage();
            System.out.println(block);
            String upload = block.upload();
            if (upload.contains("Error")) break;
        }
        System.out.println(blocks.size());
    }

}

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.message.BasicNameValuePair;
import yanwittmann.file.File;
import yanwittmann.types.LineBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Item {

    private final String itemType, itemName, wikiPage, minecraftIdentifier;
    private final File wikiPageFile;
    private boolean isRenewable = false;
    private int stackableCount = -1, restores = -1;
    private String description = "";
    private String rarity = "";
    private final String cropImageLocation;
    private File imageFile;

    public Item(String itemType, String itemName, String minecraftIdentifier, String wikiPage, File wikiPageFile, String cropImageLocation) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.minecraftIdentifier = minecraftIdentifier;
        this.wikiPage = wikiPage;
        this.wikiPageFile = wikiPageFile;
        this.cropImageLocation = cropImageLocation;
    }

    public void extractImage(BufferedImage large) throws IOException {
        if (cropImageLocation == null) return;
        String[] split = cropImageLocation.replace("px", "").split(" ");
        int posX = Math.abs(Integer.parseInt(split[0]));
        int posY = Math.abs(Integer.parseInt(split[1]));
        imageFile = new File("res/cropped/" + minecraftIdentifier.replace(":", "") + ".png");
        if (!imageFile.getParentFile().exists()) imageFile.getParentFile().mkdirs();
        ImageIO.write(cropImage(large, new Rectangle(posX, posY, 16, 16)), "png", imageFile);
    }

    public void grabDataFromWikiPage() throws IOException {
        ArrayList<String> readToArrayList = wikiPageFile.readToArrayList();
        int descriptionStatus = 0;
        LineBuilder descriptionBuilder = new LineBuilder();
        descriptionBuilder.setLinebreakSymbol(" ");
        for (int i = 0, readToArrayListSize = readToArrayList.size(); i < readToArrayListSize; i++) {
            String line = readToArrayList.get(i);

            if (line.contains("<a href=\"/wiki/Renewable_resource\" title=\"Renewable resource\">Renewable</a>") && !isRenewable) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                isRenewable = line.contains("Yes");
            } else if (line.contains("<a href=\"/wiki/Rarity\" title=\"Rarity\">Rarity color</a>") && stackableCount == -1) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                rarity = line.replaceAll("<[^>]+>", "");
            } else if (line.contains("<th>Stackable") && stackableCount == -1) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                if (line.matches(".*\\d.+"))
                    stackableCount = Integer.parseInt(line.replaceAll(".*[^\\d]+(\\d+)[^\\d]+.*", "$1"));
                else if (line.contains("Yes"))
                    stackableCount = 1;
            } else if (line.contains("<a href=\"/wiki/Food\" title=\"Food\">Restores</a>") && restores == -1) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                if (line.matches(".*\\d.*"))
                    restores = Integer.parseInt(line.replaceAll(".*?(\\d+).*", "$1"));
            } else if (descriptionStatus == 0 && line.startsWith("</tbody></table>")) {
                descriptionStatus = 1;
            } else if (descriptionStatus == 1 && line.startsWith("</div><p>")) {
                descriptionStatus = 3;
            } else if (descriptionStatus == 1 && line.startsWith("</div>")) {
                descriptionStatus = 2;
            } else if (descriptionStatus == 2 && line.startsWith("<p>")) {
                descriptionStatus = 3;
            } else if (descriptionStatus != 3 && line.startsWith("<!--")) {
                descriptionStatus = 0;
            }

            if (descriptionStatus == 3) {
                if (line.contains("<div id=\"toc\" class=\"toc\">")) {
                    descriptionStatus = 4;
                } else {
                    descriptionBuilder.append(line.replaceAll("<[^>]+>", "").replace("&amp;quot;", "\"")
                            .replace("&amp;gt;", ">").replaceAll("&#91;.&#93;", "")
                            .replace("&#8204;", "").replace("&#8260;", "/")
                            .replaceAll("\\[[^]]*]", ""));
                }
            }
        }
        description = descriptionBuilder.toString().trim();
    }

    public void uploadImage(FTPClient client) throws IOException {
        String imageUrl = MyCredentialsProvider.FTP_SERVER_PATH + "img/" + minecraftIdentifier.replace(":", "") + ".png";
        client.storeFile(imageUrl, new FileInputStream(imageFile));
    }

    public String upload() throws IOException {
        String imageUrl = MyCredentialsProvider.FTP_SERVER_URL + MyCredentialsProvider.FTP_SERVER_PATH + "img/" + minecraftIdentifier.replace(":", "") + ".png";

        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("itemType", this.itemType));
        parameters.add(new BasicNameValuePair("minecraftIdentifier", this.minecraftIdentifier));
        parameters.add(new BasicNameValuePair("itemName", this.itemName.replace("'", "\\'")));
        parameters.add(new BasicNameValuePair("description", this.description.replace("'", "\\'")));
        parameters.add(new BasicNameValuePair("wikiPage", this.wikiPage));
        parameters.add(new BasicNameValuePair("image", imageUrl));
        parameters.add(new BasicNameValuePair("isRenewable", this.isRenewable ? "1" : "0"));
        parameters.add(new BasicNameValuePair("stackableCount", this.stackableCount + ""));
        parameters.add(new BasicNameValuePair("rarity", this.rarity));
        parameters.add(new BasicNameValuePair("restores", this.restores + ""));
        return HTTPStuff.send(MyCredentialsProvider.FTP_SERVER_URL + MyCredentialsProvider.FTP_SERVER_PATH + "createitem.php", parameters);
    }

    private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public String toString() {
        return "Item{" +
               "itemType='" + itemType + '\'' +
               ", itemName='" + itemName + '\'' +
               ", wikiPage='" + wikiPage + '\'' +
               ", minecraftIdentifier='" + minecraftIdentifier + '\'' +
               ", isRenewable=" + isRenewable +
               ", stackableCount=" + stackableCount +
               ", description='" + description + '\'' +
               ", rarity='" + rarity + '\'' +
               ", restores='" + restores + '\'' +
               '}';
    }
}

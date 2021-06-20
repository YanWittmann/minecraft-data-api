import org.apache.http.message.BasicNameValuePair;
import yanwittmann.file.File;
import yanwittmann.types.LineBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private final String blockType, itemName, wikiPage, minecraftIdentifier;
    private final List<String> images, notes;
    private final File wikiPageFile;
    private boolean isRenewable = false, isLuminant = false, isTransparent = false, isFlammable = false, isFlammableFromLava = false;
    private int stackableCount = -1;
    private double blastResistance = -1, hardness = -1;
    private String bestTool = "", description = "";

    public Block(String blockType, String itemName, String minecraftIdentifier, String wikiPage, List<String> images, List<String> notes, File wikiPageFile) {
        this.blockType = blockType;
        this.itemName = itemName;
        this.minecraftIdentifier = minecraftIdentifier;
        this.wikiPage = wikiPage;
        this.images = images;
        this.notes = notes;
        this.wikiPageFile = wikiPageFile;
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
            } else if (line.contains("<th>Tool") && bestTool.length() == 0) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                bestTool = line.replaceAll("<p>", "");
                if (bestTool.contains("title=")) bestTool = bestTool.replaceAll(".+title=\"([^\"]+)\".+", "$1");
            } else if (line.contains("<a href=\"/wiki/Explosion#Blast_resistance\" title=\"Explosion\">Blast resistance</a>") && blastResistance == -1) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                if (!line.contains("Missing_value") && line.matches(".*([+-]?(?:[0-9]*[.])?[0-9]+).*"))
                    blastResistance = Double.parseDouble(line.replaceAll(".*([+-]?(?:[0-9]*[.])?[0-9]+).*", "$1"));
            } else if (line.contains("<th><a href=\"/wiki/Breaking#Blocks_by_hardness\" title=\"Breaking\">Hardness</a>") && hardness == -1) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                if (!line.contains("Missing_value") && line.matches(".*([+-]?(?:[0-9]*[.])?[0-9]+).*"))
                    hardness = Double.parseDouble(line.replaceAll(".*([+-]?(?:[0-9]*[.])?[0-9]+).*", "$1"));
            } else if (line.contains("<th><a href=\"/wiki/Light\" title=\"Light\">Luminant</a>") && !isLuminant) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                isLuminant = line.contains("Yes");
            } else if (line.contains("<th><a href=\"/wiki/Opacity\" title=\"Opacity\">Transparent</a>") && !isTransparent) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                isTransparent = line.contains("Yes");
            } else if (line.contains("<th><a href=\"/wiki/Flammable\" class=\"mw-redirect\" title=\"Flammable\">Flammable</a>") && !isFlammable) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                isFlammable = line.contains("Yes");
            } else if (line.contains("<th>Catches fire from <a href=\"/wiki/Lava\" title=\"Lava\">lava</a>") && !isFlammableFromLava) {
                do {
                    i++;
                    if (i >= readToArrayListSize) break;
                    line = readToArrayList.get(i);
                } while (!line.startsWith("<p>"));
                if (i >= readToArrayListSize) break;
                isFlammableFromLava = line.contains("Yes");
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
                            .replace("&#8204;", "").replace("&#8260;", "/"));
                }
            }
        }
        description = descriptionBuilder.toString().trim();
    }

    public String upload() throws IOException {
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("blockType", this.blockType));
        parameters.add(new BasicNameValuePair("minecraftIdentifier", this.minecraftIdentifier));
        parameters.add(new BasicNameValuePair("itemName", this.itemName.replace("'", "\\'")));
        parameters.add(new BasicNameValuePair("description", this.description.replace("'", "\\'")));
        parameters.add(new BasicNameValuePair("wikiPage", this.wikiPage));
        parameters.add(new BasicNameValuePair("images", String.join(", ", this.images)));
        parameters.add(new BasicNameValuePair("notes", String.join(", ", this.notes).replace("'", "\\'")));
        parameters.add(new BasicNameValuePair("isRenewable", this.isRenewable ? "1" : "0"));
        parameters.add(new BasicNameValuePair("stackableCount", this.stackableCount + ""));
        parameters.add(new BasicNameValuePair("bestTool", this.bestTool));
        parameters.add(new BasicNameValuePair("blastResistance", this.blastResistance + ""));
        parameters.add(new BasicNameValuePair("hardness", this.hardness + ""));
        parameters.add(new BasicNameValuePair("isLuminant", this.isLuminant ? "1" : "0"));
        parameters.add(new BasicNameValuePair("isTransparent", this.isTransparent ? "1" : "0"));
        parameters.add(new BasicNameValuePair("isFlammable", this.isFlammable ? "1" : "0"));
        parameters.add(new BasicNameValuePair("isFlammableFromLava", this.isFlammableFromLava ? "1" : "0"));
        return HTTPStuff.send(MyCredentialsProvider.FTP_SERVER_URL + MyCredentialsProvider.FTP_SERVER_PATH + "createblock.php", parameters);
    }

    @Override
    public String toString() {
        return "Block{" +
               "blockType='" + blockType + '\'' +
               ", minecraftIdentifier='" + minecraftIdentifier + '\'' +
               ", itemName='" + itemName + '\'' +
               ", description='" + description + '\'' +
               ", wikiPage='" + wikiPage + '\'' +
               ", images=" + String.join(", ", images) +
               ", notes=" + String.join(", ", notes) +
               ", isRenewable=" + isRenewable +
               ", stackableCount=" + stackableCount +
               ", bestTool=" + bestTool +
               ", blastResistance=" + blastResistance +
               ", hardness=" + hardness +
               ", isLuminant=" + isLuminant +
               ", isTransparent=" + isTransparent +
               ", isFlammable=" + isFlammable +
               ", isFlammableFromLava=" + isFlammableFromLava +
               '}';
    }
}

import java.awt.*;
import java.util.*;
public class Stenography {
    public static void clearLow(Pixel p) {
        int redVal = (p.getRed() / 4) * 4;
        int greenVal = (p.getGreen() / 4) * 4;
        int blueVal = (p.getBlue() / 4) * 4;
        p.setColor(new Color(redVal, greenVal, blueVal));
    }
    public static Picture testClearLow(Picture inputPic) {
        Picture resultPic = new Picture(inputPic);
        Pixel[][] pixelGrid = resultPic.getPixels2D();
        for (Pixel[] row : pixelGrid) {
            for (Pixel p : row) {
                clearLow(p);
            }
        }
        return resultPic;
    }
    public static void setLow(Pixel p, Color c) {
        int origRed = p.getRed();
        int origGreen = p.getGreen();
        int origBlue = p.getBlue();
        int lowRed = c.getRed() / 64;
        int lowGreen = c.getGreen() / 64;
        int lowBlue = c.getBlue() / 64;
        origRed = (origRed / 4) * 4 + lowRed;
        origGreen = (origGreen / 4) * 4 + lowGreen;
        origBlue = (origBlue / 4) * 4 + lowBlue;
        p.setColor(new Color(origRed, origGreen, origBlue));
    }
    public static Picture testSetLow(Picture basePic, Color c) {
        Picture result = new Picture(basePic);
        Pixel[][] grid = result.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel p : row) {
                setLow(p, c);
            }
        }
        return result;
    }
    public static Picture revealPicture(Picture hidden) {
        Picture output = new Picture(hidden);
        Pixel[][] outPixels = output.getPixels2D();
        Pixel[][] originalPixels = hidden.getPixels2D();
        for (int r = 0; r < outPixels.length; r++) {
            for (int c = 0; c < outPixels[0].length; c++) {
                Color col = originalPixels[r][c].getColor();
                int newRed = (col.getRed() % 4) * 64;
                int newGreen = (col.getGreen() % 4) * 64;
                int newBlue = (col.getBlue() % 4) * 64;
                outPixels[r][c].setColor(new Color(newRed, newGreen, newBlue));
            }
        }
        return output;
    }
    public static boolean canHide(Picture source, Picture secret) {
        return source.getWidth() >= secret.getWidth() && source.getHeight() >= secret.getHeight();
    }
    public static Picture hidePicture(Picture base, Picture hidden, int rowStart, int colStart) {
        Picture result = new Picture(base);
        Pixel[][] baseGrid = result.getPixels2D();
        Pixel[][] hiddenGrid = hidden.getPixels2D();
        for (int r = 0; r < hiddenGrid.length; r++) {
            for (int c = 0; c < hiddenGrid[0].length; c++) {
                int targetRow = rowStart + r;
                int targetCol = colStart + c;
                if (targetRow < baseGrid.length && targetCol < baseGrid[0].length) {
                    Color hiddenColor = hiddenGrid[r][c].getColor();
                    Color baseColor = baseGrid[targetRow][targetCol].getColor();
                    int red = (baseColor.getRed() / 4 * 4) + (hiddenColor.getRed() / 64);
                    int green = (baseColor.getGreen() / 4 * 4) + (hiddenColor.getGreen() / 64);
                    int blue = (baseColor.getBlue() / 4 * 4) + (hiddenColor.getBlue() / 64);
                    baseGrid[targetRow][targetCol].setColor(new Color(red, green, blue));
                }
            }
        }
        return result;
    }
    public static boolean isSame(Picture a, Picture b) {
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) return false;
        Pixel[][] gridA = a.getPixels2D();
        Pixel[][] gridB = b.getPixels2D();
        for (int r = 0; r < gridA.length; r++) {
            for (int c = 0; c < gridA[0].length; c++) {
                if (!gridA[r][c].getColor().equals(gridB[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }
    public static ArrayList<Point> findDifferences(Picture first, Picture second) {
        ArrayList<Point> diffs = new ArrayList<>();
        if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) return diffs;
        Pixel[][] grid1 = first.getPixels2D();
        Pixel[][] grid2 = second.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    diffs.add(new Point(c, r));
                }
            }
        }
        return diffs;
    }
    public static Picture showDifferentArea(Picture original, ArrayList<Point> changes) {
        Picture marked = new Picture(original);
        if (changes.isEmpty()) return marked;
        int minR = Integer.MAX_VALUE, maxR = Integer.MIN_VALUE;
        int minC = Integer.MAX_VALUE, maxC = Integer.MIN_VALUE;
        for (Point p : changes) {
            int row = p.y;
            int col = p.x;
            minR = Math.min(minR, row);
            maxR = Math.max(maxR, row);
            minC = Math.min(minC, col);
            maxC = Math.max(maxC, col);
        }
        Graphics2D pen = marked.createGraphics();
        pen.setColor(Color.BLUE);
        pen.drawRect(minC, minR, maxC - minC, maxR - minR);
        pen.dispose();
        return marked;
    }
    public static ArrayList<Integer> encodeString(String s) {
        s = s.toUpperCase();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            String letter = s.substring(i, i + 1);
            if (letter.equals(" ")) values.add(27);
            else values.add(alphabet.indexOf(letter) + 1);
        }
        values.add(0);
        return values;
    }
    public static String decodeString(ArrayList<Integer> codes) {
        String result = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i) == 27) {
                result = result + " ";
            } else {
                result = result
                        + alpha.substring(codes.get(i) - 1, codes.get(i));
            }
        }
        return result;
    }
    private static int[] getBitPairs(int number) {
        int[] bits = new int[3];
        for (int i = 0; i < 3; i++) {
            bits[i] = number % 4;
            number /= 4;
        }
        return bits;
    }
    public static void hideText(Picture source, String s) {
        ArrayList<Integer> encoded = encodeString(s);
        Pixel[][] pixels = source.getPixels2D();
        int idx = 0;
        for (int r = 0; r < pixels.length && idx < encoded.size(); r++) {
            for (int c = 0; c < pixels[0].length && idx < encoded.size(); c++) {
                int num = encoded.get(idx);
                int redBit = (num % 4) / 4;
                int greenBit = (num % 4) / 4;
                int blueBit = num % 4;
                Pixel p = pixels[r][c];
                int newRed = (p.getRed() / 4) * 4 + redBit;
                int newGreen = (p.getGreen() / 4) * 4 + greenBit;
                int newBlue = (p.getBlue() / 4) * 4 + blueBit;
                p.setColor(new Color(newRed, newGreen, newBlue));
                idx++;
            }
        }
    }
    public static String revealText(Picture source) {
    ArrayList<Integer> words = new ArrayList<>();
    Pixel[][] pixels = source.getPixels2D();
    for (Pixel[] row : pixels) {
        for (Pixel p : row) {
            int letter = (p.getBlue() & 3) << 4 | (p.getGreen() & 3) << 2 | (p.getRed() & 3);
            if (letter == 0) {
                return decodeString(words);
            }
            words.add(letter);
        }
    }
    return decodeString(words);
}
   public static void randomBlack(Picture target, int width, int height) {
        Pixel[][] grid = target.getPixels2D();
        int maxRow = grid.length - width;
        int maxCol = grid[0].length - height;
        if (maxRow < 0 || maxCol < 0) {
            System.out.println("Region too large for the image!");
            return;
        }
        int startRow = (int) (Math.random() * maxRow);
        int startCol = (int) (Math.random() * maxCol);
        for (int r = startRow; r < startRow + height; r++) {
            for (int c = startCol; c < startCol + width; c++) {
                if (r < grid.length && c < grid[0].length) {
                    Pixel p = grid[r][c];
                    int black = (p.getRed() + p.getGreen() + p.getBlue()) / 255;
                    p.setColor(new Color(black, black, black));
                }
            }
        }
    }
}



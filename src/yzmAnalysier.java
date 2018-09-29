import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class yzmAnalysier {

    private static Map<BufferedImage, String> trainMap = null;
    private static int index = 0;

    public static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {
            return true;
        }
        return false;
    }

    public static boolean isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() >= 600) {
            return true;
        }
        return false;
    }

    public static BufferedImage binaryzation(BufferedImage img){
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                if (isWhite(img.getRGB(x, y))){
                    img.setRGB(x,y,Color.WHITE.getRGB());
                }else {
                    img.setRGB(x,y,Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }
    public static boolean inImage(int x,int y,int width,int height){
        return  (x >= 0 && x < width && y >= 0 && y < height);
    }
    public static BufferedImage removeBackgroud(BufferedImage img)
            throws Exception {
        //BufferedImage img = ImageIO.read(new File(picFile));
        img = img.getSubimage(1, 1, img.getWidth() - 2, img.getHeight() - 2);
        img = binaryzation(img);
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                if (isBlack(img.getRGB(x, y))){//去除干扰线和点
                    int count=0;//统计附近留白
                    if (inImage(x+1,y,img.getWidth(),img.getHeight()) && isWhite(img.getRGB(x+1,y))) count++;
                    if (inImage(x-1,y,img.getWidth(),img.getHeight()) && isWhite(img.getRGB(x-1,y))) count++;
                    if (inImage(x,y+1,img.getWidth(),img.getHeight()) && isWhite(img.getRGB(x,y+1))) count++;
                    if (inImage(x,y-1,img.getWidth(),img.getHeight()) && isWhite(img.getRGB(x,y-1))) count++;
                    if (count>=3) img.setRGB(x,y,Color.WHITE.getRGB());
                }
            }
        }


        /*int width = img.getWidth();
        int height = img.getHeight();
        double subWidth = (double) width /4.0;
        for (int i = 0; i < 4; i++) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int x = (int) (1 + i * subWidth); x < (i + 1) * subWidth
                    && x < width - 1; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (isWhite(img.getRGB(x, y)) == 1){
                        continue;
                    }
                    if (map.containsKey(img.getRGB(x, y))) {
                        map.put(img.getRGB(x, y), map.get(img.getRGB(x, y)) + 1);
                    } else {
                        map.put(img.getRGB(x, y), 1);
                    }
                }
            }
            int max = 0;
            int colorMax = 0;
            for (Integer color : map.keySet()) {
                if (max < map.get(color)) {
                    max = map.get(color);
                    colorMax = color;
                }
            }

            for (int x = (int) (1 + i * subWidth); x < (i + 1) * subWidth
                    && x < width - 1; ++x) {
                for (int y = 0; y < height; ++y) {
                    Color maxc=new Color(colorMax);
                    Color imgc=new Color(img.getRGB(x,y));
                    int range=150;
                    if ((imgc.getRed()>=maxc.getRed()-range && imgc.getRed()<=maxc.getRed()+range)&&
                            (imgc.getBlue()>=maxc.getBlue()-range && imgc.getBlue()<=maxc.getBlue()+range)&&
                            (imgc.getGreen()>=maxc.getGreen()-range && imgc.getGreen()<=maxc.getGreen()+range)){
                        img.setRGB(x, y, Color.BLACK.getRGB());
                    }else {
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                    if (img.getRGB(x, y) != colorMax) {
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    } else {
                        img.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }
        }*/
        return img;
    }

    public static BufferedImage removeBlank(BufferedImage img) throws Exception {
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = 0;
        Label1: for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (isBlack(img.getRGB(x, y))) {
                    start = y;
                    break Label1;
                }
            }
        }
        Label2: for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                if (isBlack(img.getRGB(x, y))) {
                    end = y;
                    break Label2;
                }
            }
        }
        return img.getSubimage(0, start, width, end - start + 1);
    }

    public static List<BufferedImage> splitImage(BufferedImage img)
            throws Exception {
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        int width = img.getWidth();
        int height = img.getHeight();
        List<Integer> weightlist = new ArrayList<Integer>();
        for (int x = 0; x < width; ++x) {
            int count = 0;
            for (int y = 0; y < height; ++y) {
                if (isBlack(img.getRGB(x, y))) {
                    count++;
                }
            }
            weightlist.add(count);
        }
        for (int i = 0; i < weightlist.size();i++) {
            int length = 0;
            while (i < weightlist.size() && weightlist.get(i) > 0) {
                i++;
                length++;
            }
            if (length > 2) {
                subImgs.add(removeBlank(img.getSubimage(i - length, 0,
                        length, height)));
            }
        }
        return subImgs;
    }

    public static Map<BufferedImage, String> loadTrainData() throws Exception {
        if (trainMap == null) {
            Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
            File dir = new File("/home/ajacker/Desktop/yzm/train");
            File[] files = dir.listFiles();
            for (File file : files) {
                map.put(ImageIO.read(file), file.getName().charAt(0) + "");
                System.out.println("TRAIN:"+file.getName()+ "");
            }
            trainMap = map;
        }
        return trainMap;
    }

    public static String getSingleCharOcr(BufferedImage img,
                                          Map<BufferedImage, String> map) {
        String result = "#";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = width * height;
        for (BufferedImage bi : map.keySet()) {
            int count = 0;
            if (Math.abs(bi.getWidth()-width) > 2)
                continue;
            int widthmin = width < bi.getWidth() ? width : bi.getWidth();
            int heightmin = height < bi.getHeight() ? height : bi.getHeight();
            Label1: for (int x = 0; x < widthmin; ++x) {
                for (int y = 0; y < heightmin; ++y) {
                    if (isBlack(img.getRGB(x, y)) != isBlack(bi.getRGB(x, y))) {
                        count++;
                        if (count >= min)
                            break Label1;
                    }
                }
            }
            if (count < min) {
                min = count;
                result = map.get(bi);
            }
        }
        return result;
    }

    public static String getAllOcr(String file) throws Exception {
        BufferedImage img = ImageIO.read(new File(file));
        img = removeBackgroud(img);
        List<BufferedImage> listImg = splitImage(img);
        Map<BufferedImage, String> map = loadTrainData();
        String result = "";
        for (BufferedImage bi : listImg) {
            result += getSingleCharOcr(bi, map);
        }
        ImageIO.write(img, "JPG", new File("/home/ajacker/Desktop/yzm/result/" + result + ".jpg"));
        return result;
    }

    public static void downloadImage() {
        for (int i = 0; i < 300; i++) {
            try {
                utils.getValidateCode("/home/ajacker/Desktop/yzm/img/"+i+".jpg");
                System.out.println(i + "OK!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    public static void trainData() throws Exception {
        File dir = new File("/home/ajacker/Desktop/yzm/temp/");
        File[] files = dir.listFiles();
        for (File file : files) {
            BufferedImage img = ImageIO.read(new File("/home/ajacker/Desktop/yzm/temp/" + file.getName()));
            img = removeBackgroud(img);
            ImageIO.write(img, "JPG", new File("/home/ajacker/Desktop/yzm/convert/"
                    + file.getName()));

            /*List<BufferedImage> listImg = splitImage(img);
            if (listImg.size() == 4) {
                for (int j = 0; j < listImg.size(); ++j) {
                    ImageIO.write(listImg.get(j), "JPG", new File("/home/ajacker/Desktop/yzm/train/"
                             + "0未识别-" + (index++)
                            + ".jpg"));
                }
            }*/
        }
    }
    public static String tessOcr(BufferedImage img) throws Exception {
        img = removeBackgroud(img);
        ITesseract instance = new Tesseract();
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");//设置训练库的位置
        instance.setLanguage("myfont");//识别字体
        String result=instance.doOCR(img);
        result=result.replaceAll(" ","");
        if (result.length()>4) result=result.substring(0,4);
        //System.out.println(result);
        return result.toUpperCase();
    }
    public static String tessOcr(String  file) throws Exception {
        BufferedImage img = ImageIO.read(new File(file));
        img = removeBackgroud(img);
        ITesseract instance = new Tesseract();
        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");//设置训练库的位置
        instance.setLanguage("myfont");//识别字体
        String result=instance.doOCR(img);
        result=result.replaceAll(" ","");
        if (result.length()>4) result=result.substring(0,4);
        //System.out.println(result);
        return result.toUpperCase();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        downloadImage();
        for (int i = 0; i < 30; ++i) {
            String text = tessOcr("/home/ajacker/Desktop/yzm/img/" + i + ".jpg");
            System.out.println(i + ".jpg = " + text);
        }
        //trainData();
    }
}

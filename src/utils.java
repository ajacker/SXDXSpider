import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class utils {
    private static String sessionId;
    static boolean login(String xh, String pwd) {
        try {
            File file = new File("");
            //String yzmpath=file.getAbsolutePath() + File.separator + "yzm.jpg";
            String yzmpath="/home/ajacker/Desktop" + File.separator + "yzm.jpg";
            if (getValidateCode(yzmpath)){
                String result = yzmAnalysier.tessOcr(yzmpath);
                System.out.println(result);
                String parampwd = chkpwd(xh, pwd);
                String paramyzm = chkyzm(result);
                //登录
                URL url = new URL("http://bkjw.sxu.edu.cn/_data/login.aspx");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");      //设置POST方式连接
                // 发送POST请求必须设置如下两行
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //设置请求头
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Origin", "http://bkjw.sxu.edu.cn");
                connection.setRequestProperty("user-agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
                connection.setRequestProperty("Referer", "http://bkjw.sxu.edu.cn/_data/login.aspx");
                connection.setRequestProperty("Cookie", "ASP.NET_SessionId=" + sessionId);
                connection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
                //连接,也可以不用明文connect，使用下面的connection.getOutputStream()会自动connect
                connection.connect();
                // 获取URLConnection对象对应的输出流
                PrintWriter out = new PrintWriter(connection.getOutputStream());
        /*
        前端在传值时，将地址中的参数中含有的加号使用%2B替换掉（一定是大写的B），这样传到java后台时就能正确显示为+号了
        * */
                // 发送请求参数
                out.print("Sel_Type=STU" +
                        "&txt_asmcdefsddsd=" + xh +//学号
                        "&__VIEWSTATE=/wEPDwULLTE4ODAwNjU4NjBkZA==" +//固定的
                        "&__EVENTVALIDATION=/wEWAgLnybi8BAKZwe%2BvBg==" +//固定的
                        "&dsdsdsdsdxcxdfgfg=" + parampwd +//加密的密码
                        "&fgfggfdgtyuuyyuuckjg=" + paramyzm);//加密的验证码
                // flush输出流的缓冲
                out.flush();
                out.close();
                int resultCode = connection.getResponseCode();
                //System.out.println("statuscode:" + resultCode);
                if (resultCode!=200) return false;
                //System.out.println(sessionId);
                return true;
                // 定义BufferedReader输入流来读取URL的响应
            /*BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "gb2312"));
            String line, res = "";
            while ((line = in.readLine()) != null) {
                res += line;
            }*/
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    private static String chkpwd(String xh, String pwd) throws IOException, ScriptException, NoSuchMethodException {
        ScriptEngineManager m = new ScriptEngineManager();
        //获取JavaScript执行引擎
        ScriptEngine engine = m.getEngineByName("JavaScript");
        //使用管道流，将输出流转为输入流
        PipedReader prd = new PipedReader();
        PipedWriter pwt = new PipedWriter(prd);
        //设置执行结果内容的输出流
        engine.getContext().setWriter(pwt);
        //js文件的路径
        String strFile = URLDecoder.decode(Thread.currentThread().getClass().getResource("/md5.js").getPath(), "utf-8");
        //System.out.println(strFile);
        Reader reader = new FileReader(new File(strFile));
        engine.eval(reader);
        Invocable invocable = (Invocable) engine;
        return (String) invocable.invokeFunction("chkpwd", xh, pwd);//返回方法结果
    }

    private static String chkyzm(String yzm) throws IOException, ScriptException, NoSuchMethodException {
        ScriptEngineManager m = new ScriptEngineManager();
        //获取JavaScript执行引擎
        ScriptEngine engine = m.getEngineByName("JavaScript");
        //使用管道流，将输出流转为输入流
        PipedReader prd = new PipedReader();
        PipedWriter pwt = new PipedWriter(prd);
        //设置执行结果内容的输出流
        engine.getContext().setWriter(pwt);
        //js文件的路径
        String strFile = URLDecoder.decode(Thread.currentThread().getClass().getResource("/md5.js").getPath(), "utf-8");
        //System.out.println(strFile);
        Reader reader = new FileReader(new File(strFile));
        engine.eval(reader);
        Invocable invocable = (Invocable) engine;
        return (String) invocable.invokeFunction("chkyzm", yzm);//返回方法结果
    }

    static boolean getValidateCode(String saveTo) {

        try {
            URL url = new URL("http://bkjw.sxu.edu.cn/sys/ValidateCode.aspx");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setRequestProperty("Referer", "http://bkjw.sxu.edu.cn/_data/login.aspx");
            connection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            connection.connect();
            InputStream is = connection.getInputStream();
            sessionId = connection.getHeaderField("set-cookie").replaceAll(";.*", "").split("=")[1];
            //System.out.println(sessionId);
            writeToLocal(saveTo, is);
            System.out.println("验证码下载成功！");
            is.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException
     */
    private static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream File = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            File.write(bytes, 0, index);
            File.flush();
        }
        File.close();
        input.close();
    }

    /**
     * 将InputStream写入本地文件
     *
     * @param saveTo 写入本地目录
     * @param height 图片高度
     * @param width  图片宽度
     * @return 返回是否执行成功
     */
    public static boolean getClassIMG(String saveTo, int height, int width){
        try {
            //前置连接
            URL preurl = new URL("http://bkjw.sxu.edu.cn/wsxk/stu_zxjg_rpt.aspx?param_xh=");
            HttpURLConnection preconnection = (HttpURLConnection) preurl.openConnection();
            preconnection.setRequestProperty("connection", "Keep-Alive");
            preconnection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            preconnection.addRequestProperty("Cookie", "ASP.NET_SessionId=" + sessionId);
            preconnection.connect();
            preconnection.getInputStream();
            //writeToLocal("/home/ajacker/Desktop/pretest.txt", pis);
            //课表地址
            URL url = new URL("http://bkjw.sxu.edu.cn/znpk/DrawKbimg.aspx?w=" + width + "&h=" + height + "&xn=2018&xq=0&zfx=0&type=xzxjg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setRequestProperty("Referer", "http://bkjw.sxu.edu.cn//wsxk/stu_zxjg_rpt.aspx?param_xh=");
            connection.setRequestProperty("Cookie", "ASP.NET_SessionId=" + sessionId);
            connection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            connection.connect();
            InputStream is = connection.getInputStream();
            writeToLocal(saveTo, is);
            preconnection.disconnect();
            connection.disconnect();
            try {
                // 通过ImageReader来解码这个file并返回一个BufferedImage对象
                // 如果找不到合适的ImageReader则会返回null，我们可以认为这不是图片文件
                // 或者在解析过程中报错，也返回false
                Image image = ImageIO.read(new File(saveTo));
                return image != null;
            } catch (IOException ex) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
    /*public static boolean getClassIMG(String saveTo, int height, int width,String sessionId){
        try {
            //前置连接
            URL preurl = new URL("http://bkjw.sxu.edu.cn/wsxk/stu_zxjg_rpt.aspx?param_xh=");
            HttpURLConnection preconnection = (HttpURLConnection) preurl.openConnection();
            preconnection.setRequestProperty("connection", "Keep-Alive");
            preconnection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            preconnection.addRequestProperty("Cookie", "ASP.NET_SessionId=" + sessionId);
            preconnection.connect();
            preconnection.getInputStream();
            //writeToLocal("/home/ajacker/Desktop/pretest.txt", pis);
            //课表地址
            URL url = new URL("http://bkjw.sxu.edu.cn/znpk/DrawKbimg.aspx?w=" + width + "&h=" + height + "&xn=2018&xq=0&zfx=0&type=xzxjg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setRequestProperty("Referer", "http://bkjw.sxu.edu.cn//wsxk/stu_zxjg_rpt.aspx?param_xh=");
            connection.setRequestProperty("Cookie", "ASP.NET_SessionId=" + sessionId);
            connection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            connection.connect();
            InputStream is = connection.getInputStream();
            writeToLocal(saveTo, is);
            preconnection.disconnect();
            connection.disconnect();
            try {
                // 通过ImageReader来解码这个file并返回一个BufferedImage对象
                // 如果找不到合适的ImageReader则会返回null，我们可以认为这不是图片文件
                // 或者在解析过程中报错，也返回false
                Image image = ImageIO.read(new File(saveTo));
                return image != null;
            } catch (IOException ex) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }*/
}

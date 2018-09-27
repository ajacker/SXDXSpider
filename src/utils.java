import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class utils {
    /**
     * 将InputStream写入本地文件
     * @param destination 写入本地目录
     * @param input	输入流
     * @throws IOException
     */
    public static void writeToLocal(String destination, InputStream input)
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
     * @param saveTo 写入本地目录
     * @param sessionId	会话session
     * @param height 图片高度
     * @param width	图片宽度
     */
    public static void getClassIMG(String saveTo,String sessionId,int height,int width){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://bkjw.sxu.edu.cn/wsxk/stu_zxjg_rpt.aspx?param_xh=")
                .get()
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "ASP.NET_SessionId="+sessionId)
                .addHeader("DNT", "1")
                .addHeader("Host", "bkjw.sxu.edu.cn")
                .addHeader("Referer", "http://bkjw.sxu.edu.cn/wsxk/stu_zxjg.aspx")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "9792ce89-edfb-44f4-9e1e-311f9ab12b57")
                .build();
        try {
            client.newCall(request).execute();
            URL url=new URL("http://bkjw.sxu.edu.cn/znpk/DrawKbimg.aspx?w="+width+"&h="+height+"&xn=2018&xq=0&zfx=0&type=xzxjg");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setRequestProperty("Referer", "http://bkjw.sxu.edu.cn//wsxk/stu_zxjg_rpt.aspx?param_xh=");
            connection.setRequestProperty("Cookie", "ASP.NET_SessionId="+sessionId);
            connection.setRequestProperty("Host", "bkjw.sxu.edu.cn");
            connection.connect();
            InputStream is=connection.getInputStream();
            //System.out.println(connection.toString());
            writeToLocal(saveTo,is);
            System.out.println("课表下载成功！");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

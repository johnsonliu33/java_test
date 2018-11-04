package com.bitspace.food.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Administrator on 2018/3/27 0027.
 */
public class DownloadUtil {
    public static void writeResponse(HttpServletResponse response, ByteArrayOutputStream os,String fileName) throws IOException {
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        Long creatTime = System.currentTimeMillis();
       response.setHeader("Content-Disposition", "attachment;filename="+ new String((creatTime + ".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
// 测试使用
//        File f = new File("C:\\Users\\Administrator\\Desktop\\" + File.separator+new String((fileName + ".xls").getBytes()));
//        FileOutputStream fos = new FileOutputStream(f);//如果文件不存在会自动创建


        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

//                fos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
//            fos.close();
            if (bos != null)
                bos.close();
        }
    }
}

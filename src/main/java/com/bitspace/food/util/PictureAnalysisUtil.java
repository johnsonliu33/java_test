package com.bitspace.food.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class PictureAnalysisUtil {

    protected static Logger log = LoggerFactory.getLogger(PictureAnalysisUtil.class);

    //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    public static String GetImageStr(String imageName) {
        String imgFile = imageName;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //对字节数组字符串进行Base64解码并生成图片
    public static boolean GenerateImage(String imgStr,String imageName){
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = imageName;//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            LoggerUtil.error(log, e.getMessage());
            return false;
        }
    }

    /**
     * 将传入的byte[]数组转换成十六机制数的字符串
     * @param src 要转换的byte数组
     * @return 返回十六进制的字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            //将一个byte的二进制数转换成十六进制字符
            String hv = Integer.toHexString(v);
            //如果二进制数转换成十六进制数高位为0，则加入'0'字符
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 将序列化后且用十六进制字符表示的对象反序列化成对象
     * @param hexString 序列化对象的十六进制表示形式的字符串
     * @return 反序列化生成的对象
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readObject(String hexString) throws IOException,
            ClassNotFoundException {
        byte[] bytes = new byte[hexString.length() / 2];
        for(int i = 0; i < hexString.length() / 2; i++) {
            String subStr = hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}

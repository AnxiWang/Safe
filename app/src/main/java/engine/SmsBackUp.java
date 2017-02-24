package engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Created by anxi on 16-12-17.
 */

public class SmsBackUp {

    private static int index = 0;

    public static void backup(Context context, String path, CallBack callBack){

        FileOutputStream fileOutputStream = null;
        Cursor cursor = null;
        try {
            File file = new File(path);
            //获取内容解析器，并找到短信数据库中的数据
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
            //文件输出流
            fileOutputStream = new FileOutputStream(file);
            //序列化数据放入xml中
            XmlSerializer newSerializer = Xml.newSerializer();
            //xml文件的设置
            newSerializer.setOutput(fileOutputStream, "utf-8");
            newSerializer.startDocument("utf-8", true);
            newSerializer.startTag(null, "smss");
            //备份短信的总数
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }
            //开始写入
            while (cursor.moveToNext()){
                newSerializer.startTag(null, "sms");

                newSerializer.startTag(null, "address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null, "address");

                newSerializer.startTag(null, "date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null, "date");

                newSerializer.startTag(null, "type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null, "type");

                newSerializer.startTag(null, "body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null, "body");

                newSerializer.endTag(null, "sms");

                //每循环一次让进度条叠加
                index++;
                Thread.sleep(500);
                //progressdialog可以在子线程中进行
                if (callBack != null) {
                    callBack.setProgress(index);
                }
            }

            newSerializer.endTag(null, "smss");
            newSerializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fileOutputStream != null) {
                cursor.close();
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //回调，实现在传进来的控件为对话框和进度条都可以
    //定义一个接口， 定义接口中未实现的业务逻辑，传递一个实现了此接口的类对象，方法的调用
    public interface CallBack{
        public void setMax(int max);

        public void setProgress(int index);

    }



//    public static String formatData(String timeStamp) {
//
//        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
//        @SuppressWarnings("unused")
//        long lcc = Long.valueOf(timeStamp);
//        int i = Integer.parseInt(timeStamp);
//        String times = sdr.format(new Date(i * 1000L));
//        return times;
//    }
}

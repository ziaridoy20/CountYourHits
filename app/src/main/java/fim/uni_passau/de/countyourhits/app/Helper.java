package fim.uni_passau.de.countyourhits.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Nahid 002345 on 6/17/2017.
 */

public class Helper {

    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public static Bitmap stringToBitmap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static File getRootDirectory(){
        return Environment.getExternalStorageDirectory();
    }

    public static String getRootDirectoryPath(){
        return Environment.getExternalStorageDirectory().toString();
    }

    public static String convertDouble2String(double mDbl) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(mDbl);
    }

    public static String storeImage(Bitmap image) {
        //save image
        OutputStream output;
        Date dateObj = new Date();
        CharSequence currentDateTime = DateFormat.format("yyyy-MM-dd hh:mm:ss", dateObj.getTime());


        File dir = new File(Helper.getRootDirectoryPath() + "/DCIM/DirtHit/Result/");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, currentDateTime + ".jpg");
//        Toast.makeText(ConnectionActivity.this, "Image Saved to SD Card", Toast.LENGTH_SHORT).show();

        try {
            output = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}

package service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Service {
    public static boolean checkFileExist(String fileName){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String curDay = simpleDateFormat.format(new Date());

        File filePath = new File((new File(".")).getAbsolutePath() + "//logs//"+curDay+"//" +  fileName + "_" + curDay+ ".log");

        return filePath.exists();
    }
}

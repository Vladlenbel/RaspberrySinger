package playSoundFile;

import main.Database;
import java.util.HashMap;

public class ThreadInfoDownloader extends Thread{ //класс для записи в базу
    private HashMap<String,String> infoForLoad;
    private Boolean isUnload;
    private Database database = new Database();

    ThreadInfoDownloader( HashMap<String,String> infoForLoad, Boolean isUnload) {

        this.infoForLoad = infoForLoad;
        this.isUnload = isUnload;
    }

    @Override
    public void run() {
        database.loadIntoLocalDB(infoForLoad, isUnload);
    }
}



/*ALTER TABLE `raspberry_schema`.`check_information`
ADD COLUMN `uuid` VARCHAR(50) NULL AFTER `isUnload`;*/
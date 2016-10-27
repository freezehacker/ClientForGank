package com.sysu.sjk.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by sjk on 16-10-21.
 */
public class FileUtils {

    public static String directory;

    // object-->file
    public static void saveObject(String fileName, Object object) {
        File file = new File(directory, fileName);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);

            Logger.log("Save object to file.");
        } catch (IOException ioe) {

        }
    }

    // file-->object
    public static Object retrieveObject(String fileName) {
        File file = new File(directory, fileName);
        if (!file.exists()) {
            return null;
        }
        Object ret = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            ret = ois.readObject();
        } catch (Exception ioe) {

        }

        Logger.log("Retrieve object from file.");

        return ret;
    }
}

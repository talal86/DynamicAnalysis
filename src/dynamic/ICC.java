/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author talal.ahmed
 */
public class ICC {

    public static List<String> APKs = new LinkedList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {

        String cmd = "/users/talal.ahmed/Downloads/dare-1.1.0-macos/";
        int i;

        /**
         * ************************************************************************************************************
         */
        BufferedReader br = null;
        File folder = new File("/users/talal.ahmed/Downloads/first_100/first10/");
        File[] listOfFiles = folder.listFiles();
        List<String> filenames = new LinkedList<String>();
        int counter = 0;

        for (i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isHidden()) {
                if (listOfFiles[i].isFile()) {
                    filenames.add(counter, listOfFiles[i].getName());
                    counter++;
                }
            }
        }
        System.out.println("listOfFiles Length " + listOfFiles.length);
        System.out.println("Filenames Length " + filenames.size() + "\n\n");
        
        for (int a = 0; a < filenames.size(); a++) {
            System.out.println(a + "The APK name is: " + filenames.get(a));
            APKs.add(a, filenames.get(a));
            System.out.println();
        }

        /**
         * ************************************************************************************************************
         */
        for (i = 0; i < APKs.size(); i++) {
            String Foldername = APKs.get(i).substring(0, APKs.get(i).length() - 4);
            
            String mkdir = "mkdir users/talal.ahmed/ICC/" + Foldername;
            String dare = cmd + "./dare -d /users/talal.ahmed/ICC/" + Foldername + "/ /Users/talal.ahmed/Downloads/first_100/first10/" + APKs.get(i);
            String line = "null";
            //String epicmkdir ="mkdir /users/talal.ahmed/epicc/"+Foldername;
            String epicc = "java -jar /Users/talal.ahmed/Downloads/epicc-0.1/epicc-0.1.jar -apk /Users/talal.ahmed/Downloads/first_100/first10/" + APKs.get(i)
                    + " -android-directory " + "/Users/talal.ahmed/ICC/" + Foldername + "/retargeted/" + Foldername
                    + " -cp /Users/talal.ahmed/Downloads/epicc-0.1/android.jar -icc-study " + "/users/talal.ahmed/epicc/";
            Runtime run = Runtime.getRuntime();

            Process md = run.exec(mkdir);
            //Process epicmd = run.exec(epicmkdir);
            
            System.out.println("Processing APK:"+Foldername);
            Process pr = run.exec(dare);

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }

            TimeUnit.SECONDS.sleep(10);

            Process icc = run.exec(epicc);
            BufferedReader buf1 = new BufferedReader(new InputStreamReader(icc.getInputStream()));
            while ((line = buf1.readLine()) != null) {
                System.out.println(line);
            }

            String copy = "cp /users/talal.ahmed/epicc/" + Foldername + "/" + Foldername + ".txt /users/talal.ahmed/ICC_Stats";
            Process cp = run.exec(copy);

        }
    }
}

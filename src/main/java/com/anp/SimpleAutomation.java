package com.anp;

import java.io.IOException;

public class SimpleAutomation {
    public static void main(String[] args) {
        String url = "";
        String browserPath = "";
        int tab = 1;
        int delayInMinutes = 2;

        // OPEN THE TAB
        for (int i = 0; i < tab; i++) {
            try{
                new ProcessBuilder(browserPath, url).start();
                System.out.println("Tab " + (i + 1) + " opened.");
                Thread.sleep(1000);
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }

        // WAIT FOR 5 SECONDS
        try {
            Thread.sleep(1000*60*delayInMinutes);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // CHANGE THE TAB
        for (int i = 0; i < tab; i++) {
            try{
//                String appleScript = "tell application \"Google Chrome\" to tell window " + (i + 1) + " to close active tab";
                String appleScript = "tell application \"System Events\" to keystroke (ASCII character 28) using {command down, option down}";
                new ProcessBuilder("osascript", "-e", appleScript).start();
                System.out.println("Tab " + (i + 1) + " changed.");
                Thread.sleep(2000);
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }

        // CLOSE THE TAB
        for (int i = 0; i < tab; i++) {
            try {
                String appleScript = "tell application \"System Events\" to keystroke \"w\" using {command down}";
                new ProcessBuilder("osascript", "-e", appleScript).start();
                System.out.println("Tab " + (i + 1) + " closed.");
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
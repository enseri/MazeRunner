import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Stats {
    File file = new File("LevelDATA.txt");

    public Stats() {

    }

    public void SaveMap(int[] grid, int[] goombaFacing, int[] doorStats, int[] teleporterStats, int[] popUps) {
        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i != grid.length; i++) {
                pw.print(grid[i]);
                if (i != grid.length - 1)
                    pw.print(" ");
            }
            pw.println("");
            for (int i = 0; i != goombaFacing.length; i++) {
                pw.print(goombaFacing[i]);
                if (i != goombaFacing.length - 1)
                    ;
                pw.print(" ");
            }
            pw.println("");
            for (int i = 0; i != doorStats.length; i++) {
                pw.print(doorStats[i]);
                if (i != doorStats.length - 1)
                    pw.print(" ");
            }
            pw.println("");
            for (int i = 0; i != teleporterStats.length; i++) {
                pw.print(teleporterStats[i]);
                if (i != teleporterStats.length - 1)
                    pw.print(" ");
            }
            pw.println("");
            for (int i = 0; i != popUps.length; i++) {
                pw.print(popUps[i]);
                if (i != popUps.length - 1)
                    pw.print(" ");
            }
            pw.println("");

            pw.close();
        } catch (IOException e) {

        }
    }

    public static int[] ReadMap(int level) {
        try {
            int[] grid = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            for (int a = 0, i = 0; a != 900; a++, i++) {
                grid[i] = s2.nextInt();
            }
            return grid;
        } catch (

        FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadGoombaStats(int level) {
        try {
            int[] goombaStats = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            for (int a = 0; a != 900; a++) {
                goombaStats[a] = s2.nextInt();
            }
            return goombaStats;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadDoorStats(int level) {
        try {
            int[] doorStats = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            for (int a = 0; a != 900; a++) {
                doorStats[a] = s2.nextInt();
            }
            return doorStats;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadTeleporterStats(int level) {
        try {
            int[] teleporterStats = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            s2.nextLine();
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            for (int a = 0; a != 900; a++) {
                teleporterStats[a] = s2.nextInt();
            }
            return teleporterStats;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static int[] readPopUps(int level) {
        try {
            int[] popUps = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            s2.nextLine();
            s2.nextLine();
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            for (int a = 0; a != 900; a++) {
                popUps[a] = s2.nextInt();
            }
            return popUps;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

}

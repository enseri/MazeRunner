import java.io.*;
import java.util.Scanner;

public class Stats {
    File file = new File("LevelDATA.txt");

    public Stats() {

    }

    public void SaveMap(int[] grid, int[] goombaFacing, int[] doorStats, int[] teleporterStats) {
        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i != grid.length; i++) {
                pw.print(grid[i]);
                if (grid[i] < 10)
                    pw.print("9");
                pw.print(" ");
            }
            pw.println("");
            for (int i = 0; i != goombaFacing.length; i++) {
                pw.print(goombaFacing[i]);
            }
            pw.println("");
            for (int i = 0; i != doorStats.length; i++) {
                pw.print(doorStats[i]);
            }
            pw.println("");
            for (int i = 0; i != teleporterStats.length; i++) {
                pw.print(teleporterStats[i]);
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
            }
            for (int a = 0; a != 900; a++) {
                String value = s2.next();
                if(value.length() != 2)
                    break;
                int i = 0;
                while (value.substring(i + 1, i + 2).equals("9")
                        && !(grid[a] + "9").equals(value.substring(i, i + 2))) {
                    grid[a]++;
                }
                while (!value.substring(i + 1, i + 2).equals("9")
                        && !(grid[a] + "").equals(value.substring(i, i + 2))) {
                    grid[a]++;
                }
            }
            return grid;
        } catch (

        FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadGoombaStats(int level) {
        try {
            int[] grid = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            String value = s2.next();
            for (int i = 0; i < 900; i++) {
                while (!(grid[i] + "").equals(value.substring(i, i + 1))) {
                    grid[i]++;
                }
            }
            return grid;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadDoorStats(int level) {
        try {
            int[] grid = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            String value = s2.next();
            for (int i = 0; i < 900; i++) {
                while (!(grid[i] + "").equals(value.substring(i, i + 1))) {
                    grid[i]++;
                }
            }
            return grid;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static int[] ReadTeleporterStats(int level) {
        try {
            int[] grid = new int[900];
            Scanner s2 = new Scanner(new File("LevelDATA.txt"));
            s2.nextLine();
            s2.nextLine();
            s2.nextLine();
            for (int i = 1; i != level; i++) {
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
                s2.nextLine();
            }
            String value = s2.next();
            for (int i = 0; i < 900; i++) {
                while (!(grid[i] + "").equals(value.substring(i, i + 1))) {
                    grid[i]++;
                }
            }
            return grid;
        } catch (FileNotFoundException e) {

        }
        return null;
    }
}

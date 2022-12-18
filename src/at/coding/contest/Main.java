package at.coding.contest;

import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static int rowColCount = 0;
    static int coinCount = 0;

    static String[][] board;
    static int rowPacman = 0;
    static int colPacman = 0;

    static int movementCount = 0;
    static String movement = "";

    static String ghostInput = "";
    static int ghostCount = 0;

    static List<Ghost> ghosts = new ArrayList<>();
    static Pacman pacman;

    static boolean dead = false;


    public static void main(String[] args) {
        String path = "E:\\Projekte\\ccc_2022\\src\\at\\coding\\contest\\";
//        String file = "input_2\\level2_1.in";
//        String output = "output_2\\level2_222222.out";
        String file = "input_3\\level3_4.in";
        String output = "output_3\\level3_4.out";

        readFile(path + file);
        prepareGhosts();
//        outputBoard();
        refreshBoard();
//        System.out.println("=========");
//        outputBoard();
//        countCoins(path + output);
        startMovement();
        writeFile(path + output, Integer.toString(coinCount) + (dead ? " NO" : " YES"));
    }

    static void readFile(String input) {
        File file = new File(input);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line;

            boolean firstLine = true;
            int index = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine) {
                    System.out.println("Col and Row Count: " + line);
                    rowColCount = Integer.parseInt(line);

                    board = new String[rowColCount][rowColCount];

                    firstLine = false;
                    continue;
                }
                // Start Position of Pacman
                if (index == rowColCount) {
                    String[] pacmanStart = line.split(" ");

                    rowPacman = Integer.parseInt(pacmanStart[0]) - 1;
                    colPacman = Integer.parseInt(pacmanStart[1]) - 1;

                    pacman = new Pacman(rowPacman, colPacman, "");
                    index++;
                    continue;
                }
                // Movement Count
                if (index == rowColCount + 1) {
                    movementCount = Integer.parseInt(line);
                    index++;
                    continue;
                }
                // Movement
                if (index == rowColCount + 2) {
                    movement = line;
                    pacman.setMovement(movement);
                    index++;
                    continue;
                }

                if (index > rowColCount + 2) {
                    // Save Ghost Input in String and handle it later
                    ghostInput += line + ";";
                    index++;
                    continue;
                }

                int characterCount = 0;
                for (char character : line.toCharArray()) {
                    board[index][characterCount] = Character.toString(character);
                    characterCount++;
                }

                index++;
            }

            System.out.println("File read!");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void countCoins(String outputName) {

        for (int i = 0; i < rowColCount; i++) {
            for (int j = 0; j < rowColCount; j++) {
                if (board[i][j].equals("C")) {
                    coinCount++;
                }
            }
        }

        System.out.println("Coin Count: " + coinCount);

    }

    static void writeFile(String file, String toWrite) {
        File output = new File(file);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(output);
            fileWriter.write(toWrite);

            fileWriter.close();

            System.out.println("File written!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void startMovement() {
        Scanner scan = new Scanner(System.in);
        for (int i = 0; (i < movementCount); i++) {

//            debugBoard();
//            scan.nextLine();

            // Movement
            switch (movement.toCharArray()[i]) {
                case 'L' -> movePacman(pacman.getRow(), pacman.getCol() - 1);
                case 'D' -> movePacman(pacman.getRow() + 1, pacman.getCol());
                case 'R' -> movePacman(pacman.getRow(), pacman.getCol() + 1);
                case 'U' -> movePacman(pacman.getRow() - 1, pacman.getCol());
                default -> System.out.println("Oida");
            }

            for (Ghost ghost : ghosts) {
                switch (ghost.movement.toCharArray()[i]) {
                    case 'L' -> moveGhosts(ghost, ghost.getRow(), ghost.getCol() - 1);
                    case 'D' -> moveGhosts(ghost, ghost.getRow() + 1, ghost.getCol());
                    case 'R' -> moveGhosts(ghost, ghost.getRow(), ghost.getCol() + 1);
                    case 'U' -> moveGhosts(ghost, ghost.getRow() - 1, ghost.getCol());
                    default -> System.out.println("Oida");
                }
            }

            String pacmanField = pacman.getRow() + "_" + pacman.getRow();
            List<String> ghostField = new ArrayList<>();

            for (Ghost ghost : ghosts) {
                ghostField.add(ghost.getRow() + "_" + ghost.getCol());
            }

            if (ghostField.contains(pacmanField)) {
                dead = true;
                break;
            }

            if (board[pacman.getRow()][pacman.getCol()].equals("W")) {
                dead = true;
                break;
            }

            if (board[pacman.getRow()][pacman.getCol()].equals("C")) {
                coinCount++;
                board[pacman.getRow()][pacman.getCol()] = "X";
            }
        }
    }

    static void debugBoard() {
        String[][] debugBoard = board.clone();

        for (int i = 0; i < rowColCount; i++) {
            for (int j = 0; j < rowColCount; j++) {
                if(pacman.getRow() == i && pacman.getCol() == j) {
                    System.out.print(ConsoleColors.YELLOW + "P " + ConsoleColors.RESET);
                    continue;
                }
                boolean ghostFound = false;
                for(Ghost ghost : ghosts) {
                    if(ghost.getRow() == i && ghost.getCol() == j) {
                        ghostFound = true;
                        System.out.print(ConsoleColors.RED + "G " + ConsoleColors.RESET);
                    }
                }
                if(ghostFound) {
                    continue;
                }
                System.out.print(debugBoard[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------");
    }

    static void movePacman(int row, int col) {
        pacman.setRow(row);
        pacman.setCol(col);
    }

    static void moveGhosts(Ghost ghost, int row, int col) {
        ghost.setRow(row);
        ghost.setCol(col);
    }

    static void prepareGhosts() {
        String[] ghostLines = ghostInput.split(";");

        ghostCount = Integer.parseInt(ghostLines[0]);

        ghostLines = removeFromArray(ghostLines, 0);

        for (int i = 0; i < ghostCount; i++) {
            String[] ghostCoord = ghostLines[0].split(" ");

            ghosts.add(new Ghost(Integer.parseInt(ghostCoord[0]) - 1, Integer.parseInt(ghostCoord[1]) - 1, ghostLines[2]));

            ghostLines = removeFromArray(ghostLines, 0);
            ghostLines = removeFromArray(ghostLines, 0);
            ghostLines = removeFromArray(ghostLines, 0);
        }

    }

    static void outputBoard() {
        String output = "";
        for (int i = 0; i < rowColCount; i++) {
            for (int j = 0; j < rowColCount; j++) {
                output += board[i][j] + " ";
            }
            System.out.println(output);
            output = "";
        }
    }


    static String[] removeFromArray(String[] array, int index) {
        String[] newArray = new String[array.length - 1];
        for (int i = 0, k = 0; i < array.length; i++) {
            if (i != index) {
                newArray[k] = array[i];
                k++;
            }
        }

        return newArray;
    }

    static void refreshBoard() {
        for (int i = 0; i < rowColCount; i++) {
            for (int j = 0; j < rowColCount; j++) {
                if(board[i][j].equals("P") || board[i][j].equals("G")) {
                    board[i][j] = "X";
                }
            }
        }
    }
}

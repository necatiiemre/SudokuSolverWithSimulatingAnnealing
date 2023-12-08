import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int[][] sudokuArray = readSudokuFromFile("sudoku.txt");
        int[][] copiedArray = new int[9][9];
        int[][] copiedArrayNew = new int[9][9];
        copyArray(sudokuArray, copiedArray);
        simulatingAnnealing(copiedArray, copiedArrayNew, sudokuArray, 1.0 / 3, 0.00000001, 0.999999, 1000, 1);
    }

    private static int[][] readSudokuFromFile(String filename) {
        int[][] sudokuArray = new int[9][9];

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char cellChar = line.charAt(col);
                    if (Character.isDigit(cellChar)) {
                        sudokuArray[row][col] = Character.getNumericValue(cellChar);
                    } else {
                        sudokuArray[row][col] = 0;
                    }
                }
                row++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sudokuArray;
    }

    private static void printSudokuArray(int[][] sudokuArray) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                System.out.println("-------------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0) {
                    System.out.print("| ");
                }
                System.out.print(sudokuArray[i][j] + " ");
            }
            System.out.print("| ");
            System.out.println();

        }
        System.out.print("-------------------------\n");
    }

    private static void copyArray(int[][] baseArray, int[][] copiedArray) {
        for (int row = 0; row < copiedArray.length; row++) {
            for (int col = 0; col < copiedArray[0].length; col++) {
                copiedArray[row][col] = baseArray[row][col];
            }
        }
    }

    private static void createRandomInitialSolution(int[][] baseArray, int[][] copiedArray) {
        int min = 1;
        int max = 9;
        for (int blockRow = 0; blockRow < 9; blockRow += 3) {
            for (int blockCol = 0; blockCol < 9; blockCol += 3) {
                // System.out.println("Block " + (blockRow / 3 + 1) + "-" + (blockCol / 3 + 1) +
                // ":");
                ArrayList<Integer> subSudokuElements = new ArrayList<>();
                for (int i = blockRow; i < blockRow + 3; i++) {
                    for (int j = blockCol; j < blockCol + 3; j++) {
                        if (baseArray[i][j] != 0) {
                            subSudokuElements.add(baseArray[i][j]);
                        }
                    }
                }
                for (int i = blockRow; i < blockRow + 3; i++) {
                    for (int j = blockCol; j < blockCol + 3; j++) {
                        if (copiedArray[i][j] == 0) {
                            Random random = new Random();
                            int possibleValue = random.nextInt(max - min) + min;
                            while (subSudokuElements.contains(possibleValue)) {
                                possibleValue = random.nextInt(max - min + 1) + min;
                            }
                            copiedArray[i][j] = possibleValue;
                            subSudokuElements.add(possibleValue);
                        }
                    }
                }
                /*
                 * for (int is : subSudokuElements) {
                 * System.out.print(is + " ");
                 * }
                 * System.out.println();
                 */
            }
        }
        System.out.println("Random Initial Solution :");
        printSudokuArray(copiedArray);
    }

    private static int calculateCost(int[][] copiedArray) {
        int rowCost = 0;
        int colCost = 0;
        ArrayList<Integer> rowElements = new ArrayList<>();
        ArrayList<Integer> colElements = new ArrayList<>();
        ArrayList<Integer> dublicateRowElements = new ArrayList<>();
        ArrayList<Integer> dublicateColElements = new ArrayList<>();
        for (int i = 0; i < copiedArray.length; i++) {
            rowElements.clear();
            dublicateRowElements.clear();
            rowElements.add(copiedArray[i][0]);
            for (int j = 1; j < copiedArray.length; j++) {
                if (rowElements.contains(copiedArray[i][j])) {
                    if (!dublicateRowElements.contains(copiedArray[i][j])) {
                        dublicateRowElements.add(copiedArray[i][j]);
                    }
                    rowElements.add(copiedArray[i][j]);
                } else {
                    rowElements.add(copiedArray[i][j]);
                }

            }
            rowCost += dublicateRowElements.size();
        }

        for (int i = 0; i < copiedArray.length; i++) {
            colElements.clear();
            dublicateColElements.clear();
            rowElements.add(copiedArray[0][i]);
            for (int j = 0; j < copiedArray.length; j++) {
                if (colElements.contains(copiedArray[j][i])) {
                    if (!dublicateColElements.contains(copiedArray[j][i])) {
                        dublicateColElements.add(copiedArray[j][i]);
                    }
                    colElements.add(copiedArray[j][i]);
                } else {
                    colElements.add(copiedArray[j][i]);
                }

            }
            colCost += dublicateColElements.size();
        }
        return (colCost + rowCost);
    }

    private static void succesor(int[][] copiedArray, int[][] baseArray, int[][] copiedArrayNew) {

        int min = 0;
        int max = 8;
        int i = (int) (Math.random() * (max - min + 1));
        int j = (int) (Math.random() * (max - min + 1));

        i = i / 3;
        j = j / 3;

        int randomI = ((int) (Math.random() * (3))) + i * 3;
        int randomJ = ((int) (Math.random() * (3))) + j * 3;
        int randomI2 = ((int) (Math.random() * (3))) + i * 3;
        int randomJ2 = ((int) (Math.random() * (3))) + j * 3;
        while (baseArray[randomI][randomJ] != 0 || baseArray[randomI2][randomJ2] != 0) {
            randomI = ((int) (Math.random() * (3))) + i * 3;
            randomJ = ((int) (Math.random() * (3))) + j * 3;
            randomI2 = ((int) (Math.random() * (3))) + i * 3;
            randomJ2 = ((int) (Math.random() * (3))) + j * 3;

        }
        for (int k = 0; k < copiedArrayNew.length; k++) {
            for (int m = 0; m < copiedArrayNew.length; m++) {
                copiedArrayNew[k][m] = copiedArray[k][m];
            }
        }
        int temp = copiedArrayNew[randomI][randomJ];
        copiedArrayNew[randomI][randomJ] = copiedArrayNew[randomI2][randomJ2];
        copiedArrayNew[randomI2][randomJ2] = temp;
    }

    private static void undo(int[][] copiedArrayNew, int[][] copiedArray) {
        for (int h = 0; h < copiedArrayNew.length; h++) {
            for (int m = 0; m < copiedArrayNew.length; m++) {
                copiedArrayNew[h][m] = copiedArray[h][m];
            }
        }
    }

    private static void move(int[][] copiedArrayNew, int[][] copiedArray) {
        for (int h = 0; h < copiedArrayNew.length; h++) {
            for (int m = 0; m < copiedArrayNew.length; m++) {
                copiedArray[h][m] = copiedArrayNew[h][m];
            }
        }
    }

    private static void simulatingAnnealing(int[][] copiedArray, int[][] copiedArrayNew, int[][] baseArray,
            double tempMax, double tempMin, double nextTempCoef, int iMax, double k) {

        createRandomInitialSolution(baseArray, copiedArray);
        System.out.println("Initial Fault Score : " + calculateCost(copiedArray) + "\n");
        int oldCost = calculateCost(copiedArray);
        Random random = new Random();
        int iterationCount = 0;
        for (double temp = tempMax; temp >= tempMin; temp *= nextTempCoef) {
            for (int i = 0; i < iMax; i++) {
                succesor(copiedArray, baseArray, copiedArrayNew);
                int newCost = calculateCost(copiedArrayNew);
                int delta = newCost - oldCost;
                if (delta > 0) {
                    if (random.nextDouble() >= Math.exp((-delta) / (k * temp))) {
                        undo(copiedArrayNew, copiedArray);
                    } else {
                        move(copiedArrayNew, copiedArray);
                        oldCost = newCost;
                    }
                } else {
                    move(copiedArrayNew, copiedArray);
                    oldCost = newCost;
                }
                iterationCount++;
                if (oldCost == 0) {
                    System.out.println("\nFault Score of Final Solution : " + oldCost);
                    System.out.println("Found in " + iterationCount + ". iteration");
                    printSudokuArray(copiedArrayNew);
                    return;

                }

                if (iterationCount < 1000) {
                    if (iterationCount % 100 == 0) {
                        System.out.println("Iteration : " + iterationCount + " - Fault Score : " + oldCost);
                    }
                } else {
                    if (iterationCount % 25000 == 0) {
                        System.out.println("Iteration : " + iterationCount + " - Fault Score : " + oldCost);
                    }
                }

            }
        }
        printSudokuArray(copiedArrayNew);
    }

}
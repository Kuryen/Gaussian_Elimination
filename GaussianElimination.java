import java.util.*;
import java.io.*;

public class GaussianElimination {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the number of equations (n <= 10):");
            int n = scanner.nextInt();
            if (n > 10) {
                System.out.println("The number of equations must be <= 10.");
                return;
            }

            double[][] A = new double[n][n+1]; // Augmented matrix
            System.out.println("Would you like to provide a filename with coefficients? (y/n)");
            String choice = scanner.next().trim().toLowerCase();
            
            if (choice.equals("y")) {
                System.out.println("Enter filename:");
                String filename = scanner.next();
                try {
                    A = readMatrixFromFile(filename, n);
                } catch (FileNotFoundException e) {
                    System.out.println("Error, please recheck for the correct filename and try again.");
                    return;
                }
            } else {
                A = readMatrixFromUser(scanner, n);
            }
            System.out.println("Initial Augmented Matrix:");
            printMatrix(A);
            
            double[] solutions = gaussianEliminationWithPivoting(A);
            System.out.println("Solutions:");
            char variable = 'x';
            for (int i = 0; i < n; i++) {
                System.out.printf("%c = %.4f%n", (char)(variable + i), solutions[i]);
            }
        }
    }
    
    private static double[][] readMatrixFromUser(Scanner scanner, int n) {
        double[][] A = new double[n][n+1];
        System.out.println("Enter the augmented matrix row by row (include the constant term):");
        for (int i = 0; i < n; i++) {
            System.out.printf("Row %d: ", i + 1);
            for (int j = 0; j < n + 1; j++) {
                A[i][j] = scanner.nextDouble();
            }
        }
        return A;
    }
    
    private static double[][] readMatrixFromFile(String filename, int n) throws FileNotFoundException {
        double[][] A = new double[n][n+1];
        Scanner fileScanner = new Scanner(new File(filename));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n + 1; j++) {
                if (fileScanner.hasNextDouble()) {
                    A[i][j] = fileScanner.nextDouble();
                }
            }
        }
        fileScanner.close();
        return A;
    }
    
    private static double[] gaussianEliminationWithPivoting(double[][] A) {
        int n = A.length;
        for (int k = 0; k < n; k++) {
            // Find the pivot row using scaled partial pivoting
            double maxRatio = 0;
            int pivot = k;
            for (int i = k; i < n; i++) {
                double currentRatio = Math.abs(A[i][k]) / Arrays.stream(A[i], 0, n).max().getAsDouble();
                if (currentRatio > maxRatio) {
                    maxRatio = currentRatio;
                    pivot = i;
                }
            }
            // Swap rows if necessary
            if (pivot != k) {
                double[] temp = A[k];
                A[k] = A[pivot];
                A[pivot] = temp;
                System.out.printf("Pivot row %d swapped with row %d%n", pivot + 1, k + 1);
            }
            // Print the pivot and scaled ratio
            System.out.printf("Pivot row: %d, Scaled Ratio: %.4f%n", pivot + 1, maxRatio);
            // Perform elimination
            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                for (int j = k; j <= n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
            System.out.println("Matrix after step " + (k + 1) + ":");
            printMatrix(A);
        }
        
        // Back substitution
        double[] solutions = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * solutions[j];
            }
            solutions[i] = (A[i][n] - sum) / A[i][i];
        }
        return solutions;
    }
    
    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.printf("%8.4f ", val);
            }
            System.out.println();
        }
        System.out.println();
    }
}

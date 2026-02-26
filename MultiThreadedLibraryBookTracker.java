import java.io.*;
import java.util.*;

class BookCatalogException extends Exception { public BookCatalogException(String m) { super(m); } }
class InsufficientArgumentsException extends Exception { public InsufficientArgumentsException(String m) { super(m); } }
class InvalidFileNameException extends Exception { public InvalidFileNameException(String m) { super(m); } }

public class MultiThreadedLibraryBookTracker {
    private static List<String> catalogLines = new ArrayList<>();
    private static String fileName;
    private static String operation;

    public static void main(String[] args) {
        try {
            if (args.length < 2) throw new InsufficientArgumentsException("Fewer than 2 arguments.");
            
            fileName = args[0];
            operation = args[1];
            
            if (!fileName.endsWith(".txt")) throw new InvalidFileNameException("File must end with .txt");

            Thread fileThread = new Thread(new FileReaderTask());
            Thread opThread = new Thread(new OperationTask());

            // تشغيل الثرد الأول وانتظاره
            fileThread.start();
            fileThread.join(); 

            // تشغيل الثرد الثاني وانتظاره
            opThread.start();
            opThread.join(); 

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {

            System.out.println("\nThank you for using the Library Book Tracker.");
        }
    }

    // Thread1: FileReader
    static class FileReaderTask implements Runnable {
        @Override
        public void run() {
            try {
                File file = new File(fileName);
                if (!file.exists()) file.createNewFile();
                
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    catalogLines.add(reader.nextLine());
                }
                reader.close();
            } catch (IOException e) {
                System.out.println("File Error: " + e.getMessage());
            }
        }
    }

    // Thread2: OperationAnalyzer
    static class OperationTask implements Runnable {
        @Override
        public void run() {
            if (operation.contains(":")) {
                System.out.println("--- Executing Multi-Threaded Operation ---");
                catalogLines.add(operation);
                Collections.sort(catalogLines);
                try {
                    saveToFile();
                    System.out.println(">>> SUCCESS: Book added to catalog.");
                } catch (IOException e) {
                    System.out.println("Error saving catalog.");
                }
            } else {

                System.out.println(">>> Searching for: " + operation);
                System.out.printf("%-30s %-20s %-15s %5s\n", "Title", "Author", "ISBN", "Copies");
                System.out.println("----------------------------------------------------------------------");
                
                boolean found = false;
                for (String line : catalogLines) {
                    if (line.toLowerCase().contains(operation.toLowerCase())) {
                        String[] p = line.split(":");
                        System.out.printf("%-30s %-20s %-15s %5s\n", p[0], p[1], p[2], p[3]);
                        found = true;
                    }
                }
                if (!found) System.out.println("No results found.");
            }
        }
    }

    private static void saveToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String line : catalogLines) {
                writer.println(line);
            }
        }
    }
}
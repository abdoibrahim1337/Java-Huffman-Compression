import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

//so eazy
public class Main {
    public static void main(String[] args) {
        String inputText = readFromFile("input.txt");
        Map<Character, Double> probabilityMap = getProbabilityMap(inputText);
        System.out.println("Probability Map: \n");
        for (Map.Entry<Character, Double> entry : probabilityMap.entrySet()) { // printing the probability map
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        HuffmanNode root = buildHuffmanTree(probabilityMap);
        Map<Character, String> huffmanCodes = generateHuffmanCodes(root);
        System.out.println("\n Huffman Coding Table : \n");
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        String compressedText = compress(inputText, huffmanCodes);
        System.out.println("Compressed: in output.txt");
        writeToFile(compressedText,"compressed.txt");
        String decompressedText = decompress(compressedText, root);
        System.out.println("Decompressed: in decompressed.txt");
        writeToFile(decompressedText,"decompressed.txt");
    }

    public static Map<Character, Double> getProbabilityMap(String text) {
        Map<Character, Integer> charCountMap = new HashMap<>();

        for (char c : text.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }

        Map<Character, Double> probabilityMap = new HashMap<>();
        int textLength = text.length();

        for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
            char character = entry.getKey();
            int count = entry.getValue();
            double probability = (double) count / textLength;
            probabilityMap.put(character, probability);
        }

        return probabilityMap;
    }

    public static HuffmanNode buildHuffmanTree(Map<Character, Double> probabilityMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        for (Map.Entry<Character, Double> entry : probabilityMap.entrySet()) {
            char character = entry.getKey();
            int frequency = (int) (entry.getValue() * 100); // Convert probability to frequency
            priorityQueue.add(new HuffmanNode(character, frequency));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode mergedNode = new HuffmanNode('-', left.frequency + right.frequency);
            mergedNode.left = left;
            mergedNode.right = right;

            priorityQueue.add(mergedNode);
        }

        return priorityQueue.poll();
    }

    public static Map<Character, String> generateHuffmanCodes(HuffmanNode root) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        generateCodes(root, "", huffmanCodes);
        return huffmanCodes;
    }

    private static void generateCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodes) {
        if (root != null) {
            if (root.left == null && root.right == null) {
                huffmanCodes.put(root.data, code);
            }
            generateCodes(root.left, code + "0", huffmanCodes);
            generateCodes(root.right, code + "1", huffmanCodes);
        }
    }

    public static String compress(String text, Map<Character, String> huffmanCodes) {
        StringBuilder compressedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            compressedText.append(huffmanCodes.get(c));
        }
        return compressedText.toString();
    }

    public static String decompress(String compressedText, HuffmanNode root) {
        StringBuilder decompressedText = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : compressedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                decompressedText.append(current.data);
                current = root;
            }
        }
        return decompressedText.toString();
    }
    public static void writeToFile(String output, String fileName) {
        // Convert the string to bytes
        byte[] outputBytes = output.getBytes();

        // Create a Path object with the specified file name
        Path filePath = Path.of(fileName);

        try {
            // Write the bytes to the file
            Files.write(filePath, outputBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }

    }
    public static String readFromFile(String fileName) {
        // Create a Path object with the specified file name
        Path filePath = Path.of(fileName);

        try {
            // Read all lines from the file and join them into a single string
            String content = String.join("\n", Files.readAllLines(filePath));
            return content;
        } catch (IOException e) {
            System.err.println("Error reading from the file: " + e.getMessage());
            return null; // or throw an exception, depending on your error handling strategy
        }
    }
}

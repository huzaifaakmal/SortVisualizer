import javax.swing.*; // Importing Swing GUI components
import java.awt.*; // Importing AWT components for layouts and graphics
import java.awt.event.*; // Importing event handling utilities
import java.util.Arrays; // Importing utility class for array operations

public class SortVisualizer extends JFrame { // Main class extending JFrame for GUI window
    private JTextField inputField; // Text field for user to enter numbers
    private JComboBox<String> sortSelector; // Dropdown to select sorting algorithm
    private JButton startButton; // Button to trigger sorting
    private int[] values; // Array to hold the parsed input values
    private JPanel chartPanel; // Panel where bars will be drawn

    public SortVisualizer() { // Constructor to set up GUI
        setTitle("Sort Visualizer"); // Set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit program on close
        setLayout(new BorderLayout()); // Use BorderLayout for placing components

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for input components
        inputField = new JTextField(20); // Input field with 20-character width
        sortSelector = new JComboBox<>(new String[]{ // Dropdown with sorting options
            "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"
        });
        startButton = new JButton("Start"); // Button to start sorting

        inputPanel.add(new JLabel("Enter numbers:")); // Label for input field
        inputPanel.add(inputField); // Add input field
        inputPanel.add(sortSelector); // Add dropdown
        inputPanel.add(startButton); // Add button
        add(inputPanel, BorderLayout.NORTH); // Add input panel to top of window

        chartPanel = new JPanel() { // Custom JPanel for drawing bars
            protected void paintComponent(Graphics g) { // Override paintComponent
                super.paintComponent(g); // Call superclass method
                if (values != null) { // If values are not null
                    int width = getWidth() / values.length; // Width of each bar
                    int max = Arrays.stream(values).max().orElse(1); // Max value for scaling
                    int startX = (getWidth() - (width * values.length)) / 2; // Center bars
                    int paddingTop = 30; // Padding to prevent top clipping

                    for (int i = 0; i < values.length; i++) { // Loop through values
                        int height = (int)(((double)values[i] / max) * (getHeight() - paddingTop)); // Scaled height
                        int x = startX + i * width; // X position of bar
                        int y = getHeight() - height; // Y position of bar

                        g.setColor(Color.BLUE); // Set color to blue
                        g.fillRect(x, y, width - 2, height); // Draw filled rectangle
                        g.setColor(Color.BLACK); // Set color to black
                        g.drawString(String.valueOf(values[i]), x + 2, y - 5); // Draw value label
                    }
                }
            }
        };
        add(chartPanel, BorderLayout.CENTER); // Add chart panel to center

        startButton.addActionListener(e -> { // Action listener for start button
            try {
                String[] parts = inputField.getText().split(","); // Split input by comma
                values = Arrays.stream(parts).map(String::trim).mapToInt(Integer::parseInt).toArray(); // Parse to int array
                String selectedSort = (String) sortSelector.getSelectedItem(); // Get selected sort type

                new Thread(() -> { // Start sorting in a new thread
                    try {
                        switch (selectedSort) { // Call selected sorting method
                            case "Bubble Sort":
                                bubbleSort();
                                break;
                            case "Selection Sort":
                                selectionSort();
                                break;
                            case "Insertion Sort":
                                insertionSort();
                                break;
                            case "Merge Sort":
                                mergeSort(0, values.length - 1);
                                break;
                            case "Quick Sort":
                                quickSort(0, values.length - 1);
                                break;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(); // Print exception if thread is interrupted
                    }
                }).start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter comma-separated integers."); // Error dialog
            }
        });

        setSize(800, 400); // Set window size
        setLocationRelativeTo(null); // Center the window
        setVisible(true); // Make the window visible
    }

    private void bubbleSort() throws InterruptedException { // Bubble sort algorithm
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = 0; j < values.length - i - 1; j++) {
                if (values[j] > values[j + 1]) {
                    swap(j, j + 1); // Swap if out of order
                    repaintWithDelay(); // Redraw with delay
                }
            }
        }
    }

    private void selectionSort() throws InterruptedException { // Selection sort
        for (int i = 0; i < values.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < values.length; j++) {
                if (values[j] < values[minIndex]) {
                    minIndex = j; // Track smallest element
                }
            }
            if (minIndex != i) {
                swap(i, minIndex); // Swap with minimum
                repaintWithDelay();
            }
        }
    }

    private void insertionSort() throws InterruptedException { // Insertion sort
        for (int i = 1; i < values.length; i++) {
            int key = values[i];
            int j = i - 1;
            while (j >= 0 && values[j] > key) {
                values[j + 1] = values[j]; // Shift element
                j--;
                repaintWithDelay();
            }
            values[j + 1] = key; // Insert key
            repaintWithDelay();
        }
    }

    private void mergeSort(int left, int right) throws InterruptedException { // Merge sort
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
            repaintWithDelay();
        }
    }

    private void merge(int left, int mid, int right) { // Merge step
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (values[i] <= values[j]) {
                temp[k++] = values[i++];
            } else {
                temp[k++] = values[j++];
            }
        }
        while (i <= mid) temp[k++] = values[i++]; // Copy left overs
        while (j <= right) temp[k++] = values[j++];

        System.arraycopy(temp, 0, values, left, temp.length); // Copy back to original
    }

    private void quickSort(int low, int high) throws InterruptedException { // Quick sort
        if (low < high) {
            int pi = partition(low, high); // Get pivot
            repaintWithDelay();
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) { // Partition step
        int pivot = values[high]; // Pivot element
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (values[j] < pivot) {
                i++;
                swap(i, j); // Swap if less than pivot
            }
        }
        swap(i + 1, high); // Place pivot in correct location
        return i + 1;
    }

    private void swap(int i, int j) { // Swap two elements
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    private void repaintWithDelay() throws InterruptedException { // Repaint with delay for animation
        chartPanel.repaint();
        Thread.sleep(300); // Delay in milliseconds
    }

    public static void main(String[] args) { // Main method
        SwingUtilities.invokeLater(SortVisualizer::new); // Run GUI in Event Dispatch Thread
    }
}

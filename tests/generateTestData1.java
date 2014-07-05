import java.io.PrintWriter;

/**
 * Класс генерирует csv случайных данных для тестирования
 */
public class generateTestData1 {
    private static final int COUNT = 10000;
    private static final int MAX_VALUE = 999999;
    private static final int TIMESTAMP = 1404569477;

    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter("data.csv", "UTF-8");
            for (int i = 1; i <= COUNT; i++) {
                int value = (int) (Math.random() * (MAX_VALUE + 1));
                int category = (int) (Math.random() * 11);
                int timestamp = (int) (Math.random() * TIMESTAMP);
                writer.println(i + ";" + value + ";" + category + ";" + timestamp);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

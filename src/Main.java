import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.UnaryOperator;

public class Main {

    public static void main(String[] args) throws IOException {
        //dots();
        digits();
    }

    //Форма с точками
    private static void dots() {
        FormDots f = new FormDots();
        new Thread(f).start();
    }

    //Распознавание чисел
    private static void digits() throws IOException {
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
        //функция обучения 1-скорость обучения 2-функция активации 3-количество нейронов по слоям 4,5,6,7,8-количество пикселей
        NeuralNetwork nn = new NeuralNetwork(0.01, sigmoid, dsigmoid, 784, 512, 128, 32, 10);

        int samples = 60000;    //Количество фоток в /train
        BufferedImage[] images = new BufferedImage[samples];
        int[] digits = new int[samples];
        File[] imagesFiles = new File("./train").listFiles();   //считываем изображения с числами
        for (int i = 0; i < samples; i++) {
            images[i] = ImageIO.read(imagesFiles[i]);
            digits[i] = Integer.parseInt(imagesFiles[i].getName().charAt(10) + ""); //парсим число в названии
        }

        double[][] inputs = new double[samples][784];
        for (int i = 0; i < samples; i++) {
            for (int x = 0; x < 28; x++) {
                for (int y = 0; y < 28; y++) {
                    inputs[i][x + y * 28] = (images[i].getRGB(x, y) & 0xff) / 255.0;
                }
            }
        }

        int epochs = 300; //задаем проходы, чем выще, тем точнее

        PrintWriter out = new PrintWriter("output.txt");
        PrintWriter out_neurons = new PrintWriter("layers_weights.txt");

        for (int i = 1; i < epochs; i++) {
            int right = 0;
            double errorSum = 0;
            int batchSize = 100;
            for (int j = 0; j < batchSize; j++) {
                int imgIndex = (int)(Math.random() * samples);
                int[] targets = new int[10];
                int digit = digits[imgIndex];
                targets[digit] = 1;

                if (errorSum<9 & right>95) {
                    epochs=50;
                    System.out.println("Обучение завершиось по условию!"); //Выводим номер опроса
                }

                double[] outputs = nn.feedForward(inputs[imgIndex]);
                int maxDigit = 0;  //Максимальная вероятность
                double maxDigitWeight = -1;
                for (int k = 0; k < 10; k++) {
                    if(outputs[k] > maxDigitWeight) {
                        maxDigitWeight = outputs[k];
                        maxDigit = k;
                        out_neurons.println("вероятность " + k + ":" + outputs[k] + " цель: " + targets[k] + " число в изображении: " +digit);
                    }
                }
                if(digit == maxDigit) right++;
                for (int k = 0; k < 10; k++) {
                    errorSum += (targets[k] - outputs[k]) * (targets[k] - outputs[k]); //ошибка=ошибка+(верность выборва-верояность выбора)^2
                }
                nn.backpropagation(targets);
            }
            System.out.println("номер прохода: " + i + ". определил: " + right + ". ошибся: " + errorSum); //Выводим номер опроса

            out.println("номер прохода: " + i + ". определил: " + right + ". ошибся: " + errorSum );

        }
        out.close();
        out_neurons.close();

        FormDigits f = new FormDigits(nn);
        new Thread(f).start();
    }

}
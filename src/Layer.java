public class Layer {

    public int size;
    public double[] neurons;
    public double[] biases; //нейрона смещения
    public double[][] weights;  //веса

    public Layer(int size, int nextSize) {
        this.size = size;
        neurons = new double[size];
        biases = new double[size];
        weights = new double[size][nextSize];
    }

}
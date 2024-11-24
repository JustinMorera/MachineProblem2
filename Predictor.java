public interface Predictor {
    char predict(Branch pred);
    int getPreds();
    int getMispreds();
    void getContents();
}

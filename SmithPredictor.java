public class SmithPredictor implements Predictor {
    // Support variables
    int counter;
    int cutoff;
    
    // Performance tracking
    public int numPreds;
    public int numMispreds;

    public SmithPredictor(int b) {
        this.numPreds = 0;
        this.numMispreds = 0;
        this.counter = (int)Math.pow(2, b - 1); // Half of max counter value
        this.cutoff = (int)Math.pow(2, b) / 2;
    }

    @Override
    public int getPreds() {
        return this.numPreds;
    }

    @Override
    public int getMispreds() {
        return this.numMispreds;
    }

    @Override
    public void getContents() {
        System.out.println(Integer.toString(this.counter));
    }

    @Override
    public char predict(Branch branch) {
        this.numPreds++;

        char outcome = branch.action;

        char pred = 't';
        if (this.counter < this.cutoff) {
            pred = 'n';
        }

        if (outcome == 't') {
            if (this.counter < (this.cutoff * 2 - 1)) {
                this.counter++;
            }
        }
        else if (this.counter > 0) {
            this.counter--;
        }

        if (pred != outcome) {
            this.numMispreds++;
        }

        return pred;
    }
}

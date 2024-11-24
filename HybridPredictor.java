import java.util.HashMap;

public class HybridPredictor implements Predictor {
    // Parameters passed in from command line arguments
    public int k;
    public int m1;
    public int n;
    public int m2;

    // Sub-predictors
    GSharePredictor gShare;
    GSharePredictor bimodal;
    // Support variables
    HashMap<Integer, Integer> counters;
    int cutoff;


    // Performance tracking
    public int numPreds;
    public int numMispreds;

    public HybridPredictor(int k, int m1, int n, int m2) {
        this.numPreds = 0;
        this.numMispreds = 0;
        this.k = k;
        this.m1 = m1;
        this.n = n;
        this.m2 = m2;

        this.counters = new HashMap<Integer, Integer>();
        this.cutoff = 2;
        int initialVal = 1;
        for (int i = 0; i < Math.pow(2, k); i++) {
            counters.put(i, initialVal);
        }

        this.gShare = new GSharePredictor(m1, n);
        this.bimodal = new GSharePredictor(m2, 0);
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
        for (int i = 0; i < Math.pow(2, this.k); i++) {
            System.out.println(i + "\t" + Integer.toString(this.counters.get(i)));
        }
    }

    @Override
    public char predict(Branch branch) {
        this.numPreds++;

        char pred; // Prediction made later

        // Temporary predictions
        char gSharePred = gShare.predictWithoutUpdate(branch);
        char bimodalPred = bimodal.predictWithoutUpdate(branch);

        char outcome = branch.action;
        String binaryPC = Integer.toBinaryString(Integer.parseInt(branch.hexBranchPC, 16));
        // Pad with leading 0's until 32 bits
        if (binaryPC.length() < 24) {
            binaryPC = String.format("%24s", binaryPC).replace(' ', '0');
        }
        String kBits = binaryPC.substring(binaryPC.length() - 2 - this.k, binaryPC.length() - 2);
        int tableIdx = Integer.parseInt(kBits, 2);

        // Make sub-predictions
        if (this.counters.get(tableIdx) < this.cutoff) { // Bimodal selected
            pred = bimodal.predict(branch);
            gShare.updateHist(outcome);// Update gShare histReg
        }
        else { // GShare selected
            pred = gShare.predict(branch);
        }

        if (pred != outcome) {
            this.numMispreds++;
        }

        // Sub-predictors mispredicted?
        boolean gShareCorrect = gSharePred == outcome;
        boolean bimodalCorrect = bimodalPred == outcome;
        
        // Update chooser table
        if (gShareCorrect && !bimodalCorrect) {
            if (this.counters.get(tableIdx) < (this.cutoff * 2 - 1)) {
                this.counters.put(tableIdx, this.counters.get(tableIdx) + 1);
            }
        }
        else if (!gShareCorrect && bimodalCorrect) {
            if (this.counters.get(tableIdx) > 0) {
                this.counters.put(tableIdx, this.counters.get(tableIdx) - 1);
            }
        }

        return pred;
    }
}

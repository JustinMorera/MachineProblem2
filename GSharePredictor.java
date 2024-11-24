import java.util.HashMap;

public class GSharePredictor implements Predictor {
    // Parameters passed in from command line arguments
    public int m1;
    public int n;

    // Support variables
    HashMap<Integer, Integer> counters;
    int cutoff;
    public int histReg;
    public int histMask;
    
    // Performance tracking
    public int numPreds;
    public int numMispreds;

    public GSharePredictor(int m1, int n) {
        this.numPreds = 0;
        this.numMispreds = 0;
        this.m1 = m1;
        this.n = n;

        this.histReg = 0; // Initialize register to all 0's
        this.histMask = (1 << n) - 1; // Sets mask to n 1's
        this.counters = new HashMap<Integer, Integer>();
        this.cutoff = 4;
        int initialVal = 4;
        for (int i = 0; i < Math.pow(2, m1); i++) {
            counters.put(i, initialVal);
        }
    }

    public void updateHist(char outcome) {
        this.histReg >>= 1;
            if (outcome == 't') {
                this.histReg |= 1 << this.n - 1;
            }
            this.histReg &= this.histMask; // Trims register to n bits
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
        for (int i = 0; i < Math.pow(2, this.m1); i++) {
            System.out.println(i + "\t" + Integer.toString(this.counters.get(i)));
        }
    }

    @Override
    public char predict(Branch branch) {
        this.numPreds++;

        char outcome = branch.action;
        String binaryPC = Integer.toBinaryString(Integer.parseInt(branch.hexBranchPC, 16));
        // Pad with leading 0's until 32 bits
        if (binaryPC.length() < 24) {
            binaryPC = String.format("%24s", binaryPC).replace(' ', '0');
        }
        String mBits = binaryPC.substring(binaryPC.length() - 2 - this.m1, binaryPC.length() - 2);
        int tableIdx = Integer.parseInt(mBits, 2);
        if (this.n > 0) {
            tableIdx ^= (this.histReg & this.histMask);
            updateHist(outcome);
        }

        char pred = 't';
        if (this.counters.get(tableIdx) < this.cutoff) {
            pred = 'n';
        }

        if (outcome == 't') {
            if (this.counters.get(tableIdx) < (this.cutoff * 2 - 1)) {
                this.counters.put(tableIdx, this.counters.get(tableIdx) + 1);
            }
        }
        else if (this.counters.get(tableIdx) > 0) {
            this.counters.put(tableIdx, this.counters.get(tableIdx) - 1);
        }

        if (pred != outcome) {
            this.numMispreds++;
        }

        return pred;
    }

    public char predictWithoutUpdate(Branch branch) {
        String binaryPC = Integer.toBinaryString(Integer.parseInt(branch.hexBranchPC, 16));
        // Pad with leading 0's until 32 bits
        if (binaryPC.length() < 24) {
            binaryPC = String.format("%24s", binaryPC).replace(' ', '0');
        }
        String mBits = binaryPC.substring(binaryPC.length() - 2 - this.m1, binaryPC.length() - 2);
        int tableIdx = Integer.parseInt(mBits, 2);
        if (this.n > 0) {
            tableIdx ^= (this.histReg & this.histMask);
        }

        char pred = 't';
        if (this.counters.get(tableIdx) < this.cutoff) {
            pred = 'n';
        }

        return pred;
    }
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class sim {
	public static void main(String[] args) {
		// sim <simName> <B/M2/M1/K> <tracefile/N/M1> <tracefile/N> <M2>
		int b = -1; // number of counter bits used for prediction
		int k = -1; // number of pc bits used to index chooser table
		int m1 = -1; // number of PC bits used to index gshare table
		int n = -1; // number of global branch history register bits used to index gshare table
		int m2 = -1; // number of pc bits used to index bimodal table
		String file = "NULL";

		// Initialize variables
		int numPreds = 0;
		int numMispreds = 0;
		float mispredRate = 0;
		List<Branch> branches = new ArrayList<Branch>();
		Predictor predictor = null;


		// Capture command-line arguments
		if (args[0].equals("smith")) {
			b = Integer.parseInt(args[1]);
			file = args[2];
			// Initialize predictor
			predictor = new SmithPredictor(b);
		}
		else if (args[0].equals("bimodal")) {
			m2 = Integer.parseInt(args[1]);
			n = 0;
			file = args[2];
			// Initialize predictor
			predictor = new GSharePredictor(m2, n);
		}
		else if (args[0].equals("gshare")) {
			m1 = Integer.parseInt(args[1]);
			n = Integer.parseInt(args[2]);
			file = args[3];
			// Initialize predictor
			predictor = new GSharePredictor(m1, n);
		}
		else if (args[0].equals("hybrid")) {
			k = Integer.parseInt(args[1]);
			m1 = Integer.parseInt(args[2]);
			n = Integer.parseInt(args[3]);
			m2 = Integer.parseInt(args[4]);
			file = args[5];
			// Initialize predictor
			predictor = new HybridPredictor(k, m1, n, m2);
		}
		else {
			System.out.println("Invalid arguments");
			return;
		}

		

		// Scan input file for branches
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = reader.readLine()) != null) {
				String[] splitCommand = line.split(" ");
				String hexBranchPC = splitCommand[0];
				char action = splitCommand[1].charAt(0);
				branches.add(new Branch(hexBranchPC, action));
			}
		}
		catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
		}

		// Print initial setup parameters
		System.out.println("COMMAND");
		System.out.print("./sim ");
		for (int i=0; i < args.length; i++) 
			System.out.print(args[i] + " ");
		System.out.println("");

		for (Branch branch : branches) {
			predictor.predict(branch);
		}

		numPreds = predictor.getPreds();
		numMispreds = predictor.getMispreds();
		mispredRate = ((float)numMispreds / (float)numPreds) * 100;

		System.out.println("OUTPUT");
		System.out.println("number of predictions:\t\t" + numPreds);
		System.out.println("number of mispredictions:\t" + numMispreds);
		System.out.printf("misprediction rate:		%.2f%%\n", mispredRate);
		if (args[0].equals("smith")) {
			System.out.print("FINAL COUNTER CONTENT:		");
			predictor.getContents();
		}
		else if (args[0].equals("bimodal")) {
			System.out.println("FINAL BIMODAL CONTENTS");
			predictor.getContents();
		}
		else if (args[0].equals("gshare")) {
			System.out.println("FINAL GSHARE CONTENTS");
			predictor.getContents();
		}
		else if (args[0].equals("hybrid")) {
			System.out.println("FINAL CHOOSER CONTENTS");
			predictor.getContents();
		}
	}
}

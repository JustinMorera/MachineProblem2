public class Branch {
	String hexBranchPC;
    char action;
	
	public Branch(String hexBranchPC, char action) {
		this.hexBranchPC = hexBranchPC; // Hex string address
		this.action = action; // 't' or 'n'
	}

    public String toString() {
        return "hexBranchPC: " + hexBranchPC + " action: " + action;
    }
}

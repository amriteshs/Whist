// class to create instances for NPCs
public class NPCfactory {
	NPCfactory() {
		
	}
	
	private Legal legal = new Legal();
	private Smart smart = new Smart();
	
	public NPC getNPC(String name) {
		if (name.equals("Legal")) {
			return legal;
		} else if (name.equals("Smart")) {
			return smart;
		} else {
			return null;
		}
	}
}
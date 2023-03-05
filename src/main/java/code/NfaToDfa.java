package code;
import java.util.*;

import static code.NfaToDfa.State.mergeStates;

/**
 * Write your info here
 * 
 * @name Aly ElAshram
 * @id 46-1784
 * @labNumber 15
 */

public class NfaToDfa {
	public String A;
	public String I;
	public String F;
	public static class State implements Comparable<State>{
		ArrayList<String[]> transitions = new ArrayList<String[]>();

		ArrayList<String> epsilonClosure = new ArrayList<String>();
		String number;
		boolean isAccept=false;

		public State(String number) {
			this.number = number;

		}
		@Override
		public boolean equals(Object obj){
			if(obj==null)return false;
			if(obj.getClass()!=this.getClass())return false;
			final State other = (State) obj;
			if(this.number.equals(other.number)) return true;
			else return false;
		}
		@Override
		public String toString() {
			String state = this.number+";";
			for(int i=0;i<transitions.size();i++){
				state+=Arrays.deepToString(transitions.get(i));
			}

			state+=Arrays.deepToString(epsilonClosure.toArray());

			return state;
		}
		public static State mergeStates(ArrayList<State> all,String[]alphabet,String[] transitions){
			State newState ;
			String From ="";
			String to="";
			String literal = "";

			for(int i=0;i<all.size();i++){
				if(i>0){
					From+="/"+all.get(i).number;
				}
				else{
					From+=all.get(i).number;
				}
			}
			newState = new State(From);

			for(int i=0;i<alphabet.length;i++){
				literal = alphabet[i];
				boolean matched = false;
				for(int j=0;j<all.size();j++){
					String currentStateNumber = all.get(j).number;
					for(int w=0;w< transitions.length;w++){
						String singleTransition = transitions[w];
						String fromTrans = singleTransition.charAt(0)+"";
						String character = singleTransition.charAt(2)+"";
						String toTrans = singleTransition.charAt(4)+"";
						if(literal.equals(character) && currentStateNumber.equals(fromTrans)){
							matched = true;
							if(to.isEmpty()){
								to+=toTrans;
							}
							else{
								to+="/"+toTrans;
							}
						}
					}

				}

				if(!matched){
					newState.addTransition(literal,-1+"");
				}
				else {
					newState.addTransition(literal, to);
				}

			}

			return newState;

		}

		public void addTransition(String literal, String To) {
			if(literal.equals("e")) return;
			String [] trans = {literal,To};
			transitions.add(trans);
		}
		public void addEpsilonClosure(String To){
			epsilonClosure.add(To);
		}
		public void sortAndRemoveDupes(){
			HashSet<String> filtered = new HashSet<>(epsilonClosure);
			ArrayList<Integer> closure =new ArrayList<>();
			for(String string : filtered){
				closure.add(Integer.parseInt(string));
			}
			Collections.sort(closure);
			epsilonClosure.clear();
			for(Integer number:closure){
				epsilonClosure.add(number+"");
			}
		}


		@Override
		public int compareTo(State o) {
			String[] arr1 = this.number.split("/");
			String[] arr2 = o.number.split("/");

			int[] intArr1 = new int[arr1.length];
			int[] intArr2 = new int[arr2.length];

			for (int i = 0; i < arr1.length; i++) {
				intArr1[i] = Integer.parseInt(arr1[i]);
			}

			for (int i = 0; i < arr2.length; i++) {
				intArr2[i] = Integer.parseInt(arr2[i]);
			}

			Arrays.sort(intArr1);
			Arrays.sort(intArr2);


			return Integer.compare(intArr1[0], intArr2[0]);

		}
		}


	ArrayList<State> NFA = new ArrayList<>();
	ArrayList<State> DFA = new ArrayList<>();
	public NfaToDfa(String input) {
		String [] hashSplit = input.split("#");
		String[] Q = hashSplit[0].split(";");
		String[] A = hashSplit[1].split(";");
		this.A = hashSplit[1];
		String[] T=hashSplit[2].split(";");

		this.I=hashSplit[3];;
		this.F=hashSplit[4];

		//Create New States all states and add them in order
		for(int i=0;i<Q.length;i++){
		NFA.add(new State(Q[i]));
		NFA.get(i).addEpsilonClosure(i+"");
		}
		//Add All Transitions of all states
		for(int i=0;i<T.length;i++){
			String singleTransition = T[i];
			String[] split = singleTransition.split(",");
			int from =Integer.parseInt(split[0]+"");
			String literal = split[1]+"";
			String to = split[2]+"";
			if(literal.equals("e")){
				NFA.get(from).addEpsilonClosure(to);
			}else {
				NFA.get(from).addTransition(literal, to);
			}
		}
		//Create Epsilon Clousres for all the States
		boolean hasChanged = true;
		while(hasChanged) {
			hasChanged=false;
			for (int i = 0; i < NFA.size(); i++) {
				State state = NFA.get(i);
				for (int j = 1; j < state.epsilonClosure.size(); j++) {
					int index = Integer.parseInt(state.epsilonClosure.get(j));
					ArrayList<String> Add = NFA.get(index).epsilonClosure;
					if(!state.epsilonClosure.containsAll(Add)){
						//TODO:Remove Duplicates
						state.epsilonClosure.addAll(Add);
						hasChanged=true;
						state.sortAndRemoveDupes();
					}
				}
			}
			Collections.sort(NFA);
		}


			print(NFA);

			ArrayList<Integer> stateNumbers = getIntegerArray(NFA.get(Integer.parseInt(this.I)).epsilonClosure);
			ArrayList<State> epsillonClosure = new ArrayList<>();
			for(int j=0;j<stateNumbers.size();j++){
				epsillonClosure.add(NFA.get(j));
			}

			State merged = mergeStates(epsillonClosure,A,T);
			addEpsilonClosure(merged);
			DFA.add(merged);


			BuildDFA(T,A);

			print(DFA);




			}




public String sort(String a){
		String [] one = a.split("/");
		List<Integer> numsSort = new ArrayList<Integer>();
		for(String str:one){
			numsSort.add(Integer.parseInt(str));
		}
		Collections.sort(numsSort);
		String str ="";
		for(Integer number:numsSort){
			str+=number+"/";
		}
		return str.substring(0,str.length()-1);
}
public void BuildDFA(String[] T,String[]A){

		boolean added = true;
		while(added) {
			ArrayList<String[]> transitions = DFA.get(DFA.size()-1).transitions;
			added = false;
			for (int i = 0; i < transitions.size(); i++) {
				String[] singleTransition = transitions.get(i);
				String stateNumber = singleTransition[1];
				if(stateNumber.isEmpty()){
					stateNumber="-1";
				}
				State newState;
				if (stateNumber.equals("-1")) {
					newState = new State("-1");
					for(int q=0;q<A.length;q++){
						newState.addTransition(A[q],"-1");
					}
				}
				else {
					if(stateNumber.equals("1/2/4/6/7/8/9/10/12")){
						int x=0;
					}
					List<String> allStatesNumbers = Arrays.asList(stateNumber.split("/"));
					ArrayList<State> allStates = new ArrayList<State>();
					for (int j = 0; j < allStatesNumbers.size(); j++) {
						allStates.add(NFA.get(Integer.parseInt(allStatesNumbers.get(j))));
					}

					newState = new State(stateNumber);
					for (int j = 0; j < A.length; j++) {
						String literal = A[j];
						HashSet<String> To = new HashSet<>();

						for (int w = 0; w < allStates.size(); w++) {
							ArrayList<String[]> currentTrans = allStates.get(w).transitions;
							for (int r = 0; r < currentTrans.size(); r++) {
								String[] singleTrans = currentTrans.get(r);
								if (singleTrans[0].equals(literal)) {
										To.add(singleTrans[1]);
										//add epsilon closure of the added state
										ArrayList<String> eps = NFA.get(Integer.parseInt(singleTrans[1])).epsilonClosure;
										for(String string : eps){
											To.add(string);
										}

								}
							}

						}
						String To1 = "";

						for(String string : To){
							if(To1.isEmpty()){
								To1+=string;
							}
							else{
								To1+="/"+string;
							}
						}
						if(To1.isEmpty()){
							To1 = "-1";
						}
						To1 = sort(To1);
						newState.addTransition(literal,To1);

					}


				}
				if(!DFA.contains(newState)){
					added = true;
					DFA.add(newState);
				}
			}
		}

}



	public State addEpsilonClosure(State merged){
		for(int i =0;i<merged.transitions.size();i++){
			String[] trans = merged.transitions.get(i);
			String stateNumber =trans[1];
			if(!trans[1].equals("-1")){
				if(trans[1].length()==1) {
					int index = Integer.parseInt(trans[1]);
					stateNumber = "";
					for (int j = 0; j < NFA.get(index).epsilonClosure.size(); j++) {
						if (j == 0) {
							stateNumber += NFA.get(index).epsilonClosure.get(j);
						} else {
							stateNumber += "/" + NFA.get(index).epsilonClosure.get(j);
						}
					}
				}else{
					String [] all = trans[1].split("/");
					for(String string : all){
						int index = Integer.parseInt(string);
						stateNumber = "";
						for (int j = 0; j < NFA.get(index).epsilonClosure.size(); j++) {
							if (j == 0) {
								stateNumber += NFA.get(index).epsilonClosure.get(j);
							} else {
								stateNumber += "/" + NFA.get(index).epsilonClosure.get(j);
							}
						}
					}
				}

			}
			String literal = trans[0];
			merged.transitions.remove(i);
			String [] transition = new String[]{literal,stateNumber};
			merged.transitions.add(i,transition);

		}
		return merged;
	}
	public ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(String stringValue : stringArray) {
			try {
				//Convert String to Integer, and store it into integer array list.
				result.add(Integer.parseInt(stringValue));
			} catch(NumberFormatException nfe) {
				System.out.println("Could not parse " + nfe);
				}
		}
		return result;
	}



	public void print(ArrayList<State>states){
		for(int i=0;i<states.size();i++){
			System.out.println(states.get(i).toString());
		}
	}

	@Override
	public String toString() {
		Collections.sort(DFA);
	String Q="";
	String T="";
	String I="";
	String F="";

	for(State state : DFA){
	if(Q.isEmpty()){
		Q+=state.number;
	}
	else{
		Q+=";"+state.number;
	}
		for(String[] trans : state.transitions){
			T+=state.number+","+trans[0]+","+trans[1]+";";
		}

		if(state.number.contains(this.F)){
			if(F.isEmpty()) {
				F += state.number;
			}else{
				F+=";"+state.number;
			}
		}
		if(checkNum(this.I,state.number.split("/"))){
			I+=state.number;
		}

	}
	System.out.println(Q);


	return Q+"#"+this.A+"#"+T.substring(0,T.length()-1)+"#"+I+"#"+F;
	}
	public boolean checkNum(String a, String [] b){

			for(int j=0;j<b.length;j++){
				if(Integer.parseInt(a)==Integer.parseInt(b[j])){
					return true;
				}
			}
		return false;

	}
	public static void main(String[]args){
		NfaToDfa nfa = new NfaToDfa("0;1;2;3;4;5;6;7;8#m;z#0,m,4;0,m,1;0,m,6;0,m,3;0,z,2;0,z,4;0,z,6;0,z,8;1,m,5;1,m,0;1,m,1;1,m,6;1,z,3;1,z,5;1,z,0;1,z,4;1,z,6;1,z,2;1,z,1;2,m,3;2,m,5;2,m,1;2,m,6;2,z,3;2,z,1;2,z,0;2,z,6;3,m,6;3,m,8;3,m,0;3,m,1;3,z,4;3,z,6;3,z,7;3,z,1;4,m,2;4,m,4;4,m,8;4,m,5;4,m,6;4,z,0;4,z,7;4,z,4;4,z,2;4,z,1;4,z,5;4,z,3;5,m,0;5,m,6;5,m,8;5,m,4;5,m,7;5,m,5;5,m,3;5,z,2;5,z,1;5,z,4;5,z,0;5,z,5;6,m,4;6,m,0;6,m,8;6,m,2;6,m,6;6,m,3;6,z,1;6,z,5;6,z,0;6,z,3;7,m,3;7,m,2;7,m,0;7,m,7;7,z,5;7,z,7;7,z,1;7,z,3;8,m,7;8,m,1;8,m,4;8,m,0;8,z,7;8,z,4;8,z,8;8,z,5;8,z,6;8,z,2#3#2;4;7;8");
		System.out.println(nfa.toString());
	}


}

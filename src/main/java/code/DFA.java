package code;

import java.util.*;

public class DFA {

    public String[] Alphabet;
    public String Initial;
    public String Final;
    public String[] Transitions;
    private ArrayList<HashSet<String>> dfaStates;

    public DFA(String input) {

        String[] hashSplit = input.split("#");
        String[] Q = hashSplit[0].split(";");
        String[] A = hashSplit[1].split(";");
        this.Alphabet = hashSplit[1].split(";");
        this.Transitions = hashSplit[2].split(";");
        this.Initial = hashSplit[3];
        this.Final = hashSplit[4];

        String[][] NFA = new String[Q.length][Alphabet.length + 1];
        for (int i = 0; i < Q.length; i++) {
            for (int j = 0; j < Alphabet.length + 1; j++) {
                NFA[i][j] = "";
            }
        }
        //Literal controller
        for (int i = 0; i < Alphabet.length; i++) {
            String literal = Alphabet[i];
            //State controller
            for (int j = 0; j < Q.length; j++) {
                //Transition controller
                for (int w = 0; w < Transitions.length; w++) {
                    String[] singleTransition = Transitions[w].split(",");
                    String from = singleTransition[0];
                    String transitionLiteral = singleTransition[1];
                    String to = singleTransition[2];
                    if (Integer.parseInt(from) == j && transitionLiteral.equals(literal)) {
                        if (NFA[j][i].isEmpty()) {
                            NFA[j][i] = to;
                        } else {
                            NFA[j][i] += "/" + to;
                        }
                    } else if (Integer.parseInt(from) == j && transitionLiteral.equals("e")) {
                        if (NFA[j][(NFA[i].length - 1)].isEmpty()) {
                            NFA[j][(NFA[i].length - 1)] = to;
                        } else {
                            if (!NFA[j][(NFA[i].length - 1)].contains(to))
                                NFA[j][(NFA[i].length - 1)] += "/" + to;
                        }
                    }

                }
            }
        }

        // Compute epsilon closure for each state
        for (int i = 0; i < Q.length; i++) {
            HashSet<String> closure = new HashSet<String>();
            closure.add(String.valueOf(i)); // add current state to closure
            boolean changed;
            do {
                changed = false;
                for (int j = 0; j < NFA[i][Alphabet.length].length(); j++) {
                    if(!String.valueOf(NFA[i][Alphabet.length].charAt(j)).equals("/")) {
                        int nextState = Integer.parseInt(String.valueOf(NFA[i][Alphabet.length].charAt(j)));
                        if (closure.add(String.valueOf(nextState))) { // add next state to closure
                            changed = true;
                        }
                    }
                }
            } while (changed);
            NFA[i][Alphabet.length] = String.join("/", closure); // update closure in NFA array
        }

        // Compute epsilon closure for each state
        for (int i = 0; i < Q.length; i++) {
            HashSet<String> closure = new HashSet<String>();
            closure.add(String.valueOf(i)); // add current state to closure
            boolean changed;
            do {
                changed = false;
                for (int j = 0; j < NFA[i][Alphabet.length].length(); j++) {
                    if(!String.valueOf(NFA[i][Alphabet.length].charAt(j)).equals("/")) {
                        int nextState = Integer.parseInt(String.valueOf(NFA[i][Alphabet.length].charAt(j)));
                        if (closure.add(String.valueOf(nextState))) { // add next state to closure
                            changed = true;
                        }
                    }
                }
            } while (changed);
            NFA[i][Alphabet.length] = String.join("/", closure); // update closure in NFA array
        }

        String [][] dfa = new String[Q.length][Alphabet.length];
        for (int k = 0; k < Alphabet.length; k++) {
            String states = "";
            for (int i = 0; i < Q.length; i++) {
                String[] stateClosure = NFA[i][Alphabet.length].split("/");
                for (int j = 0; j < stateClosure.length; j++) {
                    int indx = Integer.parseInt(stateClosure[j]);
                    if (!NFA[indx][k].isEmpty()){
                        if (states.isEmpty()) {
                            states = NFA[indx][k];
                        }
                        else {
                            // TODO; split into array and handle contains
                            if (!states.contains(NFA[indx][k])) {
                                states = states + "/" + NFA[indx][k];
                            }
                            }
                    }
                }
                if (states.isEmpty()){
                    dfa[i][k] = "-1";
                }
                else {
                    dfa[i][k] = states;
                }
            }
        }


        System.out.println(Arrays.deepToString(dfa));
        System.out.println(Arrays.deepToString(NFA));
    }

    @Override
    public String toString(){
        return "";

    }
    public static void main(String[]args){
        DFA dfa = new DFA("0;1;2;3;4;5;6;7;8;9;10#a;b#0,e,1;1,b,2;2,e,3;3,e,4;3,e,9;4,e,5;4,e,7;5,a,6;6,e,4;6,e,9;7,b,8;8,e,4;8,e,9;9,a,10#0#10");
    }
}

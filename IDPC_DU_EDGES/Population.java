package IDPC_DU.Multifactorial_Evolutionary_Algorithm.IDPC_DU_EDGES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
    public static int POP_SIZE = 100;    
    public static int curTask;
    public static double totalFitness = 0;
    public static List<Circle> population = new ArrayList<Circle>();
    public static List<Circle> selectedForGO = new ArrayList<Circle>();

    public static void PopulationInitialize(){
        for (int i = 0; i < POP_SIZE; i++) {
            Circle c = new Circle(PreProcess.sample_node, PreProcess.css_node_out_edge);
            population.add(c);
        }

        //Calculate the factorial cost for each element
        for (Circle c : population) {
            for (int i = 0; i < Files.TASK_NUM; i++) {
                c.CalculateFactorialCost(i);
            }
        }
    }

    public static void PopulationIriteirate(){
        //Find the Factorial Rank for Each Task 
        curTask = 0;
        for (int i = 0; i < Files.TASK_NUM; i++) {
            Collections.sort(population, Circle.TaskCostComp);
            for (Circle c : population) {
                c.f_rank[i] = population.indexOf(c) + 1;
            }
            curTask++;
        }

        for (Circle c : population) {
            Circle.ScalarFitnessCalculation(c);
        }

        for (Circle c : population) {
            c.best_fit = Circle.BestFitEvaluation(c);
            c.s_factor = Circle.SkillFactorAssigning(c.s_fit);
        }

        Collections.sort(population, Circle.BestFitComp);

        //Calculate the total fitness of the population
        totalFitness = 0.0f;
        for (int i = 0; i < population.size(); i++) {
            totalFitness += population.get(i).best_fit;
        }

        //Keep the 2 best fitness individuals
        for (int j = 0; j < population.size(); j++) {
            if(population.get(j).f_rank[0] == 1){
                selectedForGO.add(population.get(j));
                break;
            }
        }
        for (int v = 0; v < population.size(); v++) {
            if(population.get(v).f_rank[1] == 1){
                selectedForGO.add(population.get(v));
                break;
            }   
        }
    }

    public static void PickingForGenOpt() {
        List<Circle> mating = new ArrayList<Circle>();
        int parentForGO = POP_SIZE/2, childNum = POP_SIZE/2;
        int rand1;
        int rand2;

        //Get total fitness of the population
        for (int i = 0; i < parentForGO; i++) {
            Roulette(population);
        }
        population.clear();

        while(childNum > 0) {
            rand1 = Main.random.nextInt(selectedForGO.size());
            rand2 = Main.random.nextInt(selectedForGO.size());
            
            mating.add(selectedForGO.get(rand1));
            mating.add(selectedForGO.get(rand2));
            GeneticMechanism.GeneticOperators(mating.get(0), mating.get(1));
            mating.clear();
            childNum -= 2;
        }
    }

    public static void Roulette(List<Circle> p) {
        double value = Main.random.nextDouble() ;

        double sum = 0;

        for (int i = 0; i < p.size(); i++) {
            sum += p.get(i).best_fit;
            if (value <= sum/totalFitness) {
                selectedForGO.add(p.get(i));
                break;
            }
        }
        return;
    }

    public static void NaturalSelection(List<Circle> p) {
        population.addAll(p);
        p.clear();
    }
}

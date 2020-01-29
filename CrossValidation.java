import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
    	List<List<Instance>> folds = new ArrayList<>();
    	int index = 0;
    	double score = 0;
    	int div = trainData.size()/k;
    	while(index < trainData.size()){
    		folds.add(trainData.subList(index, div));
    		index = div;
    		div += trainData.size()/k;
    	}
    	for(int i = 0; i < folds.size(); i++){
    		List<Instance> test = folds.get(i);
    		List<Instance> train = new ArrayList<>();
    		for(int j = 0; j < folds.size(); j++){
    			if(i != j){
    				for( Instance inst : folds.get(j)){
    					train.add(inst);
    				}
    			}
    		}
    		double acc = 0;
    		clf.train(train, v);
    		for(Instance inst: test){
    			if(clf.classify(inst.words).label == inst.label){
    				acc++;
    			}
    		}
    		score += acc/test.size();
    	}
        // TODO : Implement
        return score/k;
    }
}

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
	public Map<Label, Integer> wordCountPerLabel;
	public Map<Label, Integer> documentCountPerLabel;
	public Map<Label, Map<String, Integer>> occurrencesOfWords;
	public int vValue;
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
    	 // TODO : Implement
        // Hint: First, calculate the documents and words counts per label and store them. 
    	wordCountPerLabel = getWordsCountPerLabel(trainData);
    	documentCountPerLabel = getDocumentsCountPerLabel(trainData);
    	
        // Then, for all the words in the documents of each label, count the number of occurrences of each word.
    	occurrencesOfWords = new HashMap<Label,Map<String,Integer>>();
    	occurrencesOfWords.put(Label.POSITIVE, new HashMap<String,Integer>());
    	occurrencesOfWords.put(Label.NEGATIVE, new HashMap<String,Integer>());
    	for(Instance inst : trainData){
    		for(String str : inst.words){
    			Integer i = occurrencesOfWords.get(inst.label).get(str);
    			if(i== null){
    				occurrencesOfWords.get(inst.label).put(str, 1);
    			}else{
    				Map<String,Integer> map = occurrencesOfWords.get(inst.label);
    				map.replace(str, i+1);
    				occurrencesOfWords.replace(inst.label,map);
    			}
    		}
    	}
        // Save these information as you will need them to calculate the log probabilities later.
    	vValue = v;

        //
        // e.g.
        // Assume m_map is the map that stores the occurrences per word for positive documents
        // m_map.get("catch") should return the number of "catch" es, in the documents labeled positive
        // m_map.get("asdasd") would return null, when the word has not appeared before.
        // Use m_map.put(word,1) to put the first count in.
        // Use m_map.replace(word, count+1) to update the value
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
    	Map<Label, Integer> wordCount = new HashMap<Label,Integer>();
    	int negativeCount = 0;
    	int positiveCount = 0;
        for (Instance inst : trainData){
        	if(inst.label == Label.POSITIVE){
        		positiveCount += inst.words.size();
        	}else{
        		negativeCount += inst.words.size();
        	}
        }
        wordCount.put(Label.POSITIVE, positiveCount);
        wordCount.put(Label.NEGATIVE, negativeCount);
        return wordCount;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
    	Map<Label, Integer> docCount = new HashMap<Label,Integer>();
    	int negativeCount = 0;
    	int positiveCount = 0;
        for (Instance inst : trainData){
        	if(inst.label == Label.POSITIVE){
        		positiveCount ++;
        	}else{
        		negativeCount ++;
        	}
        }
        docCount.put(Label.POSITIVE, positiveCount);
        docCount.put(Label.NEGATIVE, negativeCount);
        return docCount;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
    	Integer numOfDocuments = documentCountPerLabel.get(Label.POSITIVE) + documentCountPerLabel.get(Label.NEGATIVE);
    	Integer count = documentCountPerLabel.get(label);
    	// TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
        return (double)count/(double)numOfDocuments;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
    	
    	Integer numOfOccurences = occurrencesOfWords.get(label).get(word);
    	if(numOfOccurences == null){
    		numOfOccurences = 0;
    	}
    	
    	double numerator = (double) numOfOccurences + 1.0;
    	double demoninator = Math.abs((double) vValue) + (double)wordCountPerLabel.get(label); 
    	
        // Calculate the probability with Laplace smoothing for word in class(label)
        return numerator/demoninator;
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
    	
    	double pOfPositive = Math.log(p_l(Label.POSITIVE));
    	double pOfNegative = Math.log(p_l(Label.NEGATIVE));
    	
    	double pOfWordGivenPositive = 0.0;
    	double pOfWordGivenNegative = 0.0;
    	for(String str : words){
    		pOfWordGivenPositive += Math.log(p_w_given_l(str,Label.POSITIVE));
    		pOfWordGivenNegative += Math.log(p_w_given_l(str,Label.NEGATIVE));
    	}
    	
    	ClassifyResult result = new ClassifyResult();
    	result.logProbPerLabel = new HashMap<Label,Double>();
    	result.logProbPerLabel.put(Label.POSITIVE, pOfPositive + 
    			pOfWordGivenPositive);
    	result.logProbPerLabel.put(Label.NEGATIVE, pOfNegative + 
    			pOfWordGivenNegative);
    	
    	if(result.logProbPerLabel.get(Label.POSITIVE) >= result.logProbPerLabel.get(Label.NEGATIVE)){
    		result.label = Label.POSITIVE;
    	}else{
    		result.label = Label.NEGATIVE;
    	}
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
        return result;
    }


}

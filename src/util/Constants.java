package util;

public class Constants {

	public static String TRAINING_SENTENCES = "data/train/train_sentences.txt";
	public static String TRAINING_LEMMAS = "data/train/train_lemmas.txt";
	public static String TRAINING_TAGS = "data/train/train_tags.txt";
	public static String TEST_SENTENCES = "data/test/thesis_test_sentences.txt";
	public static String TEST_LEMMAS = "data/test/thesis_test_lemmas.txt";
	public static String TEST_TAGS = "data/test/thesis_test_tags.txt";

	public static String FEEDING_TO_SQL = "data/feeding_to_sql";
	public static String FED_TO_SQL = "data/fed_to_sql";

	public static String RESULTS_SUBSTITUTION = "results/substitution.txt";
	public static String RESULTS_INSERTION = "results/insertion.txt";
	public static String RESULTS_DELETION = "results/deletion.txt";
	public static String RESULTS_MERGING = "results/merging.txt";
	public static String RESULTS_UNMERGING = "results/unmerging.txt";

	public static String RESULTS_ALL = "results/all.txt";

	public static int EDIT_DISTANCE_THRESHOLD = 1;
	public static double EDIT_DISTANCE_WRONG_WORD_FORM = 0.6;
	public static double EDIT_DISTANCE_SPELLING_ERROR = 0.65;
	public static double EDIT_DISTANCE_INCORRECTLY_MERGED = 0.7;
	public static double EDIT_DISTANCE_INCORRECTLY_UNMERGED = 0.7;
	public static double EDIT_DISTANCE_WRONG_WORD_SAME_POS = 0.8;
	public static double EDIT_DISTANCE_WRONG_WORD_DIFFERENT_POS = 0.95;
	public static double EDIT_DISTANCE_MISSING_WORD = 1;
	public static double EDIT_DISTANCE_UNNECESSARY_WORD = 1;

	public static int NGRAM_SIZE_UPPER = 7;
	public static int NGRAM_SIZE_LOWER = 2;

}

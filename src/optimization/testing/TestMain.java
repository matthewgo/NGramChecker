package optimization.testing;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import optimization.models.Input;
import optimization.models.Suggestion;
import optimization.testing.service.DeletionService;
import optimization.testing.service.InsertionService;
import optimization.testing.service.MergingService;
import optimization.testing.service.SubstitutionService;
import optimization.testing.service.TestErrorsProvider;
import optimization.testing.service.UnmergingService;
import util.ArrayToStringConverter;
import util.Constants;
import util.FileManager;

public class TestMain {
	static TestErrorsProvider testErrorsProvider = new TestErrorsProvider();
	static SubstitutionService subService;

	public static void main(String[] args) throws IOException, SQLException {
		FileManager fm = new FileManager(Constants.RESULTS_ALL);
		fm.createFile();
		long startTime = System.currentTimeMillis();
		for (int i = 0; i <= 55; i++) {
			Input testError = testErrorsProvider.getTestErrors().get(i);
			if (testError.getNgramSize() > 1) {
				checkGrammar(testError, i, fm);
				fm.writeToFile("\n");
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("All-in-all Total Grammar Checking Time Elapsed: " + (endTime - startTime));
		fm.close();
	}

	private static void checkGrammar(Input testError, int lineNumber, FileManager fm) throws IOException, SQLException {

		System.out.println("Writing Suggestions to Files");
		fm.writeToFile(lineNumber + ": Words: " + ArrayToStringConverter.convert(testError.getWords()) + " \nPOS: "
				+ ArrayToStringConverter.convert(testError.getPos()) + "\nLemmas: "
				+ ArrayToStringConverter.convert(testError.getLemmas()) + " " + testError.getWords().length);
		System.out.println("Words: " + ArrayToStringConverter.convert(testError.getWords()) + " \nPOS: "
				+ ArrayToStringConverter.convert(testError.getPos()) + "\nLemmas: "
				+ ArrayToStringConverter.convert(testError.getLemmas()) + " " + testError.getWords().length);
		long startTime = System.currentTimeMillis();
		List<Suggestion> suggestions = checkGrammarRecursive(testError, Constants.NGRAM_SIZE_UPPER, fm);
		long endTime = System.currentTimeMillis();
		System.out.println("Total Grammar Checking Time Elapsed: " + (endTime - startTime));
		fm.writeToFile("Total Grammar Checking Time Elapsed: " + (endTime - startTime));
	}

	private static List<Suggestion> checkGrammarRecursive(Input input, int ngramSize, FileManager fm)
			throws SQLException, IOException {

		List<Suggestion> allSuggestions = new ArrayList<>();

		if (ngramSize < 2)
			return null;
		if (ngramSize > input.getWords().length)
			ngramSize = input.getWords().length;

		for (int i = 0; i + ngramSize - 1 < input.getWords().length; i++) {
			String[] wArr = Arrays.copyOfRange(input.getWords(), i, i + ngramSize);
			String[] pArr = Arrays.copyOfRange(input.getPos(), i, i + ngramSize);
			String[] lArr = Arrays.copyOfRange(input.getLemmas(), i, i + ngramSize);
			Input subInput = new Input(wArr, pArr, lArr, ngramSize);
			List<Suggestion> suggestions = new ArrayList<>();

			long startTime = System.currentTimeMillis();
			List<Suggestion> subSuggestions = SubstitutionService.performTask(subInput, ngramSize);
			long endTime = System.currentTimeMillis();
			System.out.println("Substitution Elapsed: " + (endTime - startTime));
			fm.writeToFile("Substitution Elapsed: " + (endTime - startTime));
			if (subSuggestions == null) {// ngram is grammatically correct
				System.out.println("Grammatically Correct");
				fm.writeToFile("Grammatically Correct");
			} else {
				if (subSuggestions.size() > 0) {
					for (Suggestion s : subSuggestions) {
						System.out.println("Subs: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
						fm.writeToFile("Subs: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
					}
				}
				if (subSuggestions != null) {
					startTime = System.currentTimeMillis();
					List<Suggestion> insSuggestions = InsertionService.performTask(subInput, ngramSize);
					endTime = System.currentTimeMillis();
					System.out.println("Insertion Elapsed: " + (endTime - startTime));
					fm.writeToFile("Insertion Elapsed: " + (endTime - startTime));
					for (Suggestion s : insSuggestions) {
						System.out.println("Ins: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
						fm.writeToFile("Ins: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
					}

					startTime = System.currentTimeMillis();
					List<Suggestion> delSuggestions = DeletionService.performTask(subInput, ngramSize);
					endTime = System.currentTimeMillis();
					System.out.println("Deletion Elapsed: " + (endTime - startTime));
					fm.writeToFile("Deletion Elapsed: " + (endTime - startTime));
					for (Suggestion s : delSuggestions) {
						System.out.println("Del: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
						fm.writeToFile("Del: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
					}

					startTime = System.currentTimeMillis();
					MergingService mergingService = new MergingService(subInput, ngramSize);
					List<Suggestion> merSuggestions = mergingService.performTask();
					endTime = System.currentTimeMillis();
					System.out.println("Merging Elapsed: " + (endTime - startTime));
					fm.writeToFile("Merging Elapsed: " + (endTime - startTime));
					for (Suggestion s : merSuggestions) {
						System.out.println("Mer: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
						fm.writeToFile("Mer: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
					}

					startTime = System.currentTimeMillis();
					List<Suggestion> unmSuggestions = UnmergingService.performTask(subInput, ngramSize);
					endTime = System.currentTimeMillis();
					System.out.println("Unmerging Elapsed: " + (endTime - startTime));
					fm.writeToFile("Unmerging Elapsed: " + (endTime - startTime));
					for (Suggestion s : unmSuggestions) {
						System.out.println("Unm: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
						fm.writeToFile("Unm: " + s.getEditDistance() + " " + s.getPosSuggestion() + " baseFreq: "
								+ s.getFrequency() + " " + s.isHybrid() + " "
								+ ArrayToStringConverter.convert(s.getTokenSuggestions()) + " index: "
								+ s.getAffectedIndex());
					}

					if (subSuggestions.size() == 0 && insSuggestions.size() == 0 && delSuggestions.size() == 0
							&& merSuggestions.size() == 0) {
						// System.out.println("Recurse to " + (ngramSize - 1));
						suggestions = checkGrammarRecursive(subInput, ngramSize - 1, fm);
						if (suggestions != null)
							allSuggestions.addAll(suggestions);
					}
				}

			}
		}

		return allSuggestions;
	}

}

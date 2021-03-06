package working;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashMap;

import org.tinylog.Logger;

public class Ops {

	private static final String DELIMITER = ",";
	private static HashMap<Character,ArrayList<Ledger>> transactionHashMap = new HashMap<>();
	private static ArrayList<Ledger> expenseTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> savingsTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> investmentTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> emergencyReserveTransactionArrayList = new ArrayList<>();

	private static BigDecimal monthlyExpenseGoal = new BigDecimal(0);
	private static BigDecimal monthlySavingsGoal = new BigDecimal(0);
	private static BigDecimal monthlyInvestmentGoal = new BigDecimal(0);
	private static BigDecimal monthlyEmergencyReserveGoal = new BigDecimal(0);

	private static String budgetGoalString = null;

	protected static BigDecimal [] totalMonthlyTransactionGoal;
	protected static BigDecimal [] totalMonthlyExpenses;

	protected static HashMap<Integer, String> expenseHashTblForLineChart = new HashMap<>();


	private static HashMap<Character,ArrayList<Ledger>> addTransactionArrayListsToHashMap(char transactionChoice) throws IllegalArgumentException{
		switch(transactionChoice) {
			case 'E':
				transactionHashMap.put(transactionChoice,expenseTransactionArrayList);
				break;
			case 'S':
				transactionHashMap.put(transactionChoice,savingsTransactionArrayList);
				break;
			case 'I':
				transactionHashMap.put(transactionChoice,investmentTransactionArrayList);
				break;
			case 'R':
				transactionHashMap.put(transactionChoice, emergencyReserveTransactionArrayList);
				break;
			default:
				Logger.error("--- Error occurred in the AddTransactionArrayListsToHashTbl method");
				throw new IllegalArgumentException("--- Error occurred in the AddTransactionArrayListsToHashTbl method");
		}
		return transactionHashMap;
	}

	private static void addToAppropriateArrayList(Ledger ledger) throws IllegalArgumentException {
		Character transactionChoice = ledger.GetTransactionChoice();
		switch(transactionChoice) {
			case 'E':
				expenseTransactionArrayList.add(ledger);
				break;
			case 'S':
				savingsTransactionArrayList.add(ledger);
				break;
			case 'I':
				investmentTransactionArrayList.add(ledger);
				break;
			case 'R':
				emergencyReserveTransactionArrayList.add(ledger);
				break;
			default:
				Logger.error("--- Error occurred in the AddToAppropriateArrayList method");
				throw new IllegalArgumentException("--- Error occurred in the AddToAppropriateArrayList method");
		}
	}

	// Adds up transactions per date
	private static void addToExpenseLineChartHashMap(ArrayList<Ledger> expenses) {
		int resultCount = 0;
		BigDecimal dailyExpense = new BigDecimal(0);
		String tmpDate = null;
		for(Ledger transaction : expenses) {
			if(tmpDate == null) {
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = transaction.GetTransactionAmount();
			}
			// If we move onto the next date
			else if(!tmpDate.equals(transaction.GetTransactionDate())){
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = new BigDecimal(0);
				dailyExpense = dailyExpense.add(transaction.GetTransactionAmount());
			}	
			else {
				dailyExpense = dailyExpense.add(transaction.GetTransactionAmount());
				transaction.SetTransactionAmount(dailyExpense);
			}
			String hashValue = transaction.GetTransactionAmount() + ":" + transaction.GetTransactionDate();
			expenseHashTblForLineChart.put(resultCount,hashValue);
			resultCount++;
		}
	}

	// Parse txt -> add to appropriate arraylist -> add to hash table
	private static void parseTxt() throws NullPointerException, NumberFormatException, StringIndexOutOfBoundsException  {
		Character transactionChoice = '\0';
		try (BufferedReader br = Files.newBufferedReader(Paths.get(Gui.filePath))) {
			String line;
			while ((line = br.readLine()) != null) {

				String[] esirTransaction = line.split(DELIMITER);

				transactionChoice = esirTransaction[0].charAt(0);
				String transactionTitle = esirTransaction[1];
				BigDecimal transactionAmount = new BigDecimal(esirTransaction[2]);
				String transactionDate = esirTransaction[3];

				Ledger ledgerTransaction = new Ledger(transactionChoice,transactionTitle,transactionAmount,transactionDate);
				addToAppropriateArrayList(ledgerTransaction);
				addTransactionArrayListsToHashMap(transactionChoice);
			}

		} catch (IOException ioe) {
			Logger.error("--- IOException occurred in parseTxt method.");
			ioe.printStackTrace();
			System.exit(1);
		} catch (IllegalArgumentException iae) {
			Logger.error("--- IllegalArgumentException occurred in the parseTxt method, check the AddToAppropriateArrayList and AddTransactionArrayListsToHashMap methods");
			iae.printStackTrace();
			System.exit(1);
		} catch (Exception ex) {
			Logger.error("--- Exception occurred in parseTxt method.");
			ex.printStackTrace();
			System.exit(1);
		}
		addToExpenseLineChartHashMap(expenseTransactionArrayList);
	}

	// Establishes monthly income then breaks down input into a 50/40/5/5 split for budgeting reasons.
	private static void monthlyBudgetRatio() {
		BigDecimal monthlyIncome = BigDecimal.valueOf(5000.00);
		BigDecimal ratioOfMonthlyIncomeForExpenses = BigDecimal.valueOf(0.50);
		BigDecimal ratioOfMonthlyIncomeForSavings = BigDecimal.valueOf(0.40);
		BigDecimal ratioOfMonthlyIncomeForInvestmentAndEmergencies = BigDecimal.valueOf(0.05);

		monthlyExpenseGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForExpenses).setScale(2, RoundingMode.CEILING);
		monthlySavingsGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForSavings).setScale(2, RoundingMode.CEILING);
		monthlyInvestmentGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);
		monthlyEmergencyReserveGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);

		totalMonthlyTransactionGoal = new BigDecimal[]{monthlyExpenseGoal,monthlySavingsGoal,monthlyInvestmentGoal,monthlyEmergencyReserveGoal};

		budgetGoalString = "With a 50/40/5/5 split of Expenses, Savings, Investing and Emergencies your budget "
				+ "looks like: " + "\n"
				+ "Expense: $" + monthlyExpenseGoal + "\n" 
				+ "Savings: $" + monthlySavingsGoal + "\n" 
				+ "Invest: $" + monthlyInvestmentGoal + "\n"
				+ "Emergency Reserve: $" + monthlyEmergencyReserveGoal + "\n";
	}

	private static BigDecimal totalMonthlyExpenses(ArrayList<Ledger> expenses) {
		BigDecimal monthlyExpenses = new BigDecimal(0);
		for(Ledger transaction : expenses) {
			monthlyExpenses = monthlyExpenses.add(transaction.GetTransactionAmount());
		}
		return monthlyExpenses;
	}

	private static BigDecimal totalMonthlySavings(ArrayList<Ledger> savings) {
		BigDecimal monthlySavings = new BigDecimal(0);
		for(Ledger transaction : savings) {
			monthlySavings = monthlySavings.add(transaction.GetTransactionAmount());
		}
		return monthlySavings;
	}

	private static BigDecimal totalMonthlyInvestments(ArrayList<Ledger> investments) {
		BigDecimal monthlyInvestments = new BigDecimal(0);
		for(Ledger transaction : investments) {
			monthlyInvestments = monthlyInvestments.add(transaction.GetTransactionAmount());
		}
		return monthlyInvestments;
	}

	private static BigDecimal totalMonthlyForEmergencyReserve(ArrayList<Ledger> emergencies) {
		BigDecimal monthlyEmergencyReserveSetAside = new BigDecimal(0);
		for(Ledger transaction : emergencies) {
			monthlyEmergencyReserveSetAside = monthlyEmergencyReserveSetAside.add(transaction.GetTransactionAmount());
		}
		return monthlyEmergencyReserveSetAside;	
	}

	private static String totalMonthlySpendingString(BigDecimal monthlyExpenseTotal,BigDecimal 
			monthlySavingsTotal,BigDecimal monthlyInvestmentTotal,BigDecimal monthlyEmergencyReserveTotal) {
		return MessageFormat.format("This month, you spent in total, Expense: ${0}, Saving: ${1}, Investment: ${2}, Emergency: ${3}" + "\n",
				monthlyExpenseTotal, monthlySavingsTotal, monthlyInvestmentTotal, monthlyEmergencyReserveTotal);
	}

	private static String remainingMoneyString(BigDecimal remainingExpenseAmount,
			BigDecimal remainingSavingsAmount, BigDecimal remainingInvestmentAmount,BigDecimal remainingEmergencyReserveAmount) {
		return MessageFormat.format("After calculating the monthly expenses, savings, investments and emergencies based on a 50/40/5/5 breakdown, " + "\n" + 
				"Expense: ${0}" + "\n" + 
				"Savings: ${1}" + "\n" + 
				"Invest: ${2}" + "\n" +
				"Emergency Reserve: ${3}" + "\n",
				remainingExpenseAmount,remainingSavingsAmount, remainingInvestmentAmount, remainingEmergencyReserveAmount);
	}

	private static void writeResultsToTxtFile(String totalMonthlySpendingString, String remainingMoneyString) {
		try {
			String homeFolder = System.getProperty("user.home");
			File monthlyBudgetFile = new File(homeFolder, "monthly_budget_results.txt");
			Logger.info("### monthly_budget_results.txt was created");
			FileWriter monthlyBudgetFileWriter = new FileWriter(monthlyBudgetFile);
			String textForFile = budgetGoalString + totalMonthlySpendingString + remainingMoneyString;

			monthlyBudgetFileWriter.write(textForFile);
			monthlyBudgetFileWriter.close();

			Logger.info("### Results were written to monthly_budget_results.txt file within the users 'home' directory"
					+ " and the file has closed.");
		}
		catch(IOException e) {
			Logger.error("--- An error occurred creating and writing to file due to an IOException.");
			e.printStackTrace();
		}
	}

	private static void calculateRemainingMoneyAfterESIRAndWriteResultsToTxt() {
		BigDecimal monthlyExpenseTotal = totalMonthlyExpenses(expenseTransactionArrayList);
		BigDecimal monthlySavingsTotal = totalMonthlySavings(savingsTransactionArrayList);
		BigDecimal monthlyInvestmentTotal = totalMonthlyInvestments(investmentTransactionArrayList);
		BigDecimal monthlyEmergencyReserveTotal = totalMonthlyForEmergencyReserve(emergencyReserveTransactionArrayList);

		totalMonthlyExpenses = new BigDecimal[]{monthlyExpenseTotal, monthlySavingsTotal, monthlyInvestmentTotal, monthlyEmergencyReserveTotal};

		BigDecimal remainingExpenseAmount = monthlyExpenseGoal.subtract(monthlyExpenseTotal);
		BigDecimal remainingSavingsAmount = monthlySavingsGoal.subtract(monthlySavingsTotal);
		BigDecimal remainingInvestmentAmount = monthlyInvestmentGoal.subtract(monthlyInvestmentTotal);
		BigDecimal remainingEmergencyReserveAmount = monthlyEmergencyReserveGoal.subtract(monthlyEmergencyReserveTotal);

		String totalMonthlySpendingString = totalMonthlySpendingString(monthlyExpenseTotal, monthlySavingsTotal, monthlyInvestmentTotal, monthlyEmergencyReserveTotal);
		String remainingMoneyString = remainingMoneyString(remainingExpenseAmount, remainingSavingsAmount, remainingInvestmentAmount, remainingEmergencyReserveAmount);
		writeResultsToTxtFile(totalMonthlySpendingString, remainingMoneyString);
	}

	private static String formatTransactionString(String transaction) {
		transaction = transaction.replaceAll("\\s", "");
		Character transactionChoice = transaction.charAt(0);
		Character transactionChoiceToUpperCase = Character.toUpperCase(transactionChoice);
		return transaction.replaceFirst("["+ transactionChoice + "]", transactionChoiceToUpperCase.toString());
	}

	// Method used by the GUI's 'write' button
	protected static void writeTransactionToTxtFile(String transaction) throws IOException {
		FileWriter fileWriter = new FileWriter(Gui.filePath,true);
		String finalTransaction = formatTransactionString(transaction);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			Logger.info("### Writing [" + finalTransaction + "] to txt file...");
			bufferedWriter.write(finalTransaction);
			bufferedWriter.newLine();
			Logger.info("### Transactions was successfully written to txt file.");
		}
		finally {
			bufferedWriter.flush();
			bufferedWriter.close();
		}
	}

	// Driving method of Opsclass
	public static void OpsMainDriver() {
		parseTxt();
		monthlyBudgetRatio();
		calculateRemainingMoneyAfterESIRAndWriteResultsToTxt();
	}
}

package com.transactions;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import com.transactions.model.Transaction;
import com.transactions.service.Analysis;
import com.transactions.service.CSVLoader;
import com.transactions.service.impl.CSVLoaderImpl;

public class App {

	public static void main(String[] args) throws IOException, ParseException {
		CSVLoader loader = new CSVLoaderImpl();
		Analysis transactionAnalyser = new Analysis();

		String inputFile = "./src/main/resources/transactions.csv";
		String merchant = "Kwik-E-Mart";
		String startDateTime = "20/08/2018 12:00:00";
		String endDateTime = "20/08/2018 13:00:00";

		if (args.length == 4) {
			inputFile = args[0];
			merchant = args[1];
			startDateTime = args[2];
			endDateTime = args[3];

		} 

		Collection<Transaction> transactions = loader.loadTransactions(inputFile);
		DoubleSummaryStatistics dss = transactionAnalyser.analyzeTransactions(transactions, merchant, startDateTime,
				endDateTime);
		System.out.println(String.format("Number of transactions    = %d",dss.getCount()));
		System.out.println(String.format("Average Transaction Value = %.2f",dss.getAverage()));
		System.exit(0);

	}

}

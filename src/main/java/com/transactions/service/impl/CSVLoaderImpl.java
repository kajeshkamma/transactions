package com.transactions.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.transactions.model.Transaction;
import com.transactions.model.TransactionType;
import com.transactions.service.CSVLoader;
import au.com.bytecode.opencsv.CSVReader;

public class CSVLoaderImpl implements CSVLoader {

	/**
	 * To log error and debug messages.
	 */
	private static final Logger logger = Logger.getLogger("CSVLoaderImpl");

	@Override
	public List<Transaction> loadTransactions(String csvFile) throws IOException, ParseException {
		List<Transaction> transactions = new ArrayList<>();

		Map<String, Integer> transactionMap = new HashMap<>();

		try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
			boolean isFirstLine = true;
			String[] eachLine = reader.readNext();

			while (eachLine != null) {
				if (isFirstLine) {
					isFirstLine = false;
				} else {

					Transaction transaction = new Transaction();
					transaction.setID(eachLine[0]);
					transaction.setDate(CSVLoader.ddMMyyyyhhmmss.parse(eachLine[1]));
					transaction.setAmount(Double.parseDouble(eachLine[2]));
					transaction.setMerchant(eachLine[3].trim());

					String paymentType = eachLine[4].trim();
					if ("PAYMENT".equalsIgnoreCase(paymentType)) {
						transaction.setTransactionType(TransactionType.PAYMENT);
						transactionMap.put(transaction.getID(), transactions.size());
						transactions.add(transaction);

					} else if ("REVERSAL".equalsIgnoreCase(paymentType)) {
						transaction.setTransactionType(TransactionType.REVERSAL);
						if (eachLine.length > 5) {
							Integer reverseTransaction = transactionMap.get(eachLine[5].trim());
							if (reverseTransaction != null) {
								boolean isRemoved = transactions.remove(transactions.get(reverseTransaction));
								if (!isRemoved) {
									logger.log(Level.INFO,
											String.format(
													"Not able to remove original transaction with id '%s' from collection, REVERSAL transaction id is '%s' ",
													reverseTransaction, eachLine[0]));
								}
							} else {
								logger.log(Level.SEVERE,
										String.format(
												"Original transaction not found for the REVERSAL transaction '%s'",
												eachLine[0]));
							}
						} else {
							logger.log(Level.SEVERE,
									String.format("Original transaction id is missing in the REVERSAL transaction '%s'",
											eachLine[0]));
						}
					}

				}
				eachLine = reader.readNext();
			}

		}

		return transactions;
	}

}

package caspars.qa.base.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Transactions {
	private HashMap<String, TimeWatch> transactions;

	public Transactions() {
		transactions = new HashMap<String, TimeWatch>();
	}


	public void delete(String name) {
		if (!transactions.containsKey(name)) {
			Logs.printError("Transaction '" + name + "' does not exists");
		} else {
			transactions.remove(name);
		}
	}


	public void end(String name) {
		if (!transactions.containsKey(name)) {
			Logs.printError("Transaction '" + name + "' does not exists");
		} else {
			TimeWatch timer = transactions.get(name);
			timer.stop();
			long elapsed = timer.getElapsedTime();
			Logs.printACT("TRANSACTION", "End Transaction '" + name + "'  - Elapsed Time: " + elapsed + " mSec");
			transactions.remove(name);
		}
	}


	public void end(String name, String filename) {
		if (!transactions.containsKey(name)) {
			Logs.printError("Transaction '" + name + "' does not exists");
		} else {
			TimeWatch timer = transactions.get(name);
			timer.stop();
			long elapsed = timer.getElapsedTime();
			Logs.printACT("TRANSACTION", "End Transaction '" + name + "'  - Elapsed Time: " + elapsed + " mSec");
			try {
				DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date d = new Date();
				String time = timeFormat.format(d);
				String data = dateFormat.format(d);
				String line = data + ";" + time + ";" + name + ";" + elapsed + "\n";
				Files.write(Paths.get(filename), line.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				Logs.printError("Error during write to file '" + filename + "'");
			}
			transactions.remove(name);
		}
	}


	public String getFormattedTransactions(String group) {
		String ret = "";
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date d = new Date();
		String time = timeFormat.format(d);
		String data = dateFormat.format(d);
		Iterator<Entry<String, TimeWatch> > it = transactions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TimeWatch> pair = it.next();
			TimeWatch tw = pair.getValue();
			ret += data + ";" + time + ";" + group + ";" + pair.getKey() + ";" + tw.getElapsedTime() + "\n";
			// it.remove();
		}
		return ret;
	}


	public void printTransactions() {
		Logs.printStartTransaction();
		Iterator<Entry<String, TimeWatch> > it = transactions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TimeWatch> pair = it.next();
			TimeWatch tw = pair.getValue();
			Logs.printTransaction(pair.getKey(), tw.getElapsedTime());
			// it.remove();
		}
	}


	public void start(String name) {
		if (transactions.containsKey(name)) {
			Logs.printError("Transaction <" + name + "> already exists");
		} else {
			TimeWatch timer = new TimeWatch();
			transactions.put(name, timer);
			Logs.printACT("TRANSACTION", "Start Transaction '" + name + "'");
		}
	}


	@Override
	public String toString() {
		String ret = "";
		Iterator<Entry<String, TimeWatch> > it = transactions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TimeWatch> pair = it.next();
			TimeWatch tw = pair.getValue();
			ret += pair.getKey() + "\t" + tw.getElapsedTime() + "\n";
			// it.remove();
		}
		return ret;
	}
}
package com.makzk.cb.chatfilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements most of the plugin functionality for message filtering
 * 
 * @author makzk <me@makzk.com>
 *
 */
public class MessageFilterer {
	private String originalMessage;
	private String filteredMessage;
	private String filterString;
	
	// http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
	private static final String ipAddrPattern = 
			// repeat the first part 3 times
			new String(new char[3]).replace("\0", "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.") +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

	/**
	 * @param message
	 *            The message to filter
	 * @param filterString
	 *            The string to replace the filtered strings
	 */
	public MessageFilterer(String message, String filterString) {
		originalMessage = message;
		filteredMessage = message;

		this.filterString = filterString;
	}

	/**
	 * @return The filtered message, with the applied filters (using the
	 *         methods)
	 */
	public String getFilteredMsg() {
		return filteredMessage;
	}

	/**
	 * Applies the IP filter 
	 */
	public void filterIP() {
		filteredMessage = filteredMessage.replaceAll(ipAddrPattern, filterString);
	}

	/**
	 * Applies the uppercase filter
	 * 
	 * @param maxAmount
	 *            If the amount of uppercase is more than this, the filter will
	 *            be applied
	 * @param upcasePattern
	 *            The regular expression to use to detect uppercase letters
	 */
	public void filterUpcases(int maxAmount, String upcasePattern) {
		int i = 0;
		Matcher m = Pattern.compile(upcasePattern).matcher(filteredMessage);
		while (m.find()) {
			i++;
		}

		if (i > maxAmount) {
			filteredMessage = filteredMessage.toLowerCase();
		}
	}

	/**
	 * Applies a filter based on regular expression
	 * 
	 * @param regex
	 *            The regular expression to use
	 */
	public void filterRegex(String regex) {
		filteredMessage = replace(filteredMessage, regex);
	}

	/**
	 * Applies a filter based on a regular expression array
	 * 
	 * @param regex
	 *            The regular expressions to filter
	 */
	public void filterRegex(String[] regex) {
		for (int i = 0; i < regex.length; i++) {
			filterRegex(regex[i]);
		}
	}

	/**
	 * Checks if the applied (or not) filters were effective
	 * 
	 * @return true if the original message
	 */
	public boolean isFiltered() {
		return originalMessage != filteredMessage;
	}

	/**
	 * Changes the actual filter string, that will replace the filtered strings.
	 * 
	 * @param filterString
	 *            The new filter string
	 */
	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	// replace case insensitive
	private String replace(String msg, String pattern) {
		if (msg.toLowerCase().contains(pattern.toLowerCase()))
			return msg.replaceAll("(?i)" + pattern, filterString);
		return msg;
	}
}

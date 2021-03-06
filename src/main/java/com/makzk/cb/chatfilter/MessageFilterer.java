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
			"...".replace(".", "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.") +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	private static final String alwaysFiltered = "redcraft";

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
		return filteredMessage.replaceAll(alwaysFiltered, filterString);
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
	 * @param maxAmount If the amount of uppercase is more than this, the filter will
	 *                  be applied
	 * @param upcasePattern The regular expression to use to detect uppercase letters
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
	 * @param regex The regular expression to use
	 */
	public void filterRegex(String regex) {
		filteredMessage = replace(filteredMessage, regex);
	}

	/**
	 * Applies a filter based on a regular expression array
	 * 
	 * @param regex The regular expressions to filter
	 */
	public void filterRegex(String[] regex) {
		for (int i = 0; i < regex.length; i++) {
			filterRegex(regex[i]);
		}
	}
	
	/**
	 * Applies a filter based on regular expression, with a specific string
	 * 
	 * @param regex The regular expression to use.
	 * @param replace The string that will replace the filtered strings
	 */
	public void filterRegex(String regex, String replace) {
		filteredMessage = replace(filteredMessage, regex, replace);
	}

	/**
	 * Applies a filter based on a regular expression array with a specific string
	 * 
	 * @param regex The regular expressions to filter
	 * @param replace The string that will replace the filtered strings
	 */
	public void filterRegex(String[] regex, String replace) {
		for (int i = 0; i < regex.length; i++) {
			filterRegex(regex[i], replace);
		}
	}
	
	

	/**
	 * Checks if the applied (or not) filters were effective
	 * 
	 * @return true if the original message
	 */
	public boolean isFiltered() {
		return !originalMessage.equals(filteredMessage);
	}

	/**
	 * Changes the actual filter string, that will replace the filtered strings.
	 * 
	 * @param filterString The new filter string
	 */
	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	/**
	 * Replaces a string from a pattern, case insensitive, with string of this object.
	 * 
	 * @param msg The string to replace
	 * @param pattern The pattern to search
	 * @return The string replaced
	 */
	private String replace(String msg, String pattern) {
		if (msg.toLowerCase().contains(pattern.toLowerCase()))
			return msg.replaceAll("(?i)" + pattern, filterString);
		return msg;
	}
	
	/**
	 * Replaces a string from a pattern, case insensitive, with a specific string.
	 * 
	 * @param msg The string to replace
	 * @param pattern The pattern to search
	 * @param replace The string that will replace the matched strings
	 * @return The string replaced
	 */
	private String replace(String msg, String pattern, String replace) {
		if (msg.toLowerCase().contains(pattern.toLowerCase()))
			return msg.replaceAll("(?i)" + pattern, replace);
		return msg;
	}
}

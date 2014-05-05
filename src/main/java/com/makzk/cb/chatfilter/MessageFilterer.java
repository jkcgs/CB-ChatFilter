package com.makzk.cb.chatfilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageFilterer {
	private String originalMessage;
	private String filteredMessage;
	private String filterString;
	private String ipPattern;
	
	public MessageFilterer (String message, String filterString) {
		originalMessage = message;
		filteredMessage = message;
		
		this.filterString = filterString;
		
		ipPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	}
	
	public String getFilteredMsg() {
		return filteredMessage;
	}
	
	public void filterIP() {
		// Not working D:
		filteredMessage = replace(filteredMessage, ipPattern);
	}
	
	public void filterUpcases(int maxAmount, String upcasePattern) {
		int i = 0;
        Matcher m = Pattern.compile(upcasePattern).matcher(filteredMessage);
        while (m.find()) {
        	i++;
        }
        
        if(i >= maxAmount){
            filteredMessage = filteredMessage.toLowerCase();
        }
	}
	
	public void filterRegex(String regex) {
		filteredMessage = replace(filteredMessage, regex);
	}
	
	public void filterRegex(String[] regex) {
		for (int i = 0; i < regex.length; i++) {
			filterRegex(regex[i]);
		}
	}
	
	public boolean isFiltered() {
		return originalMessage != filteredMessage;
	}
	
	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}
    
    // replace case insensitive
    private String replace(String msg, String pattern){
        if(msg.toLowerCase().contains(pattern.toLowerCase()))
            return msg.replaceAll("(?i)" + pattern, filterString);
        return msg;
    }
}

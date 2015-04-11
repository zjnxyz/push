package com.xtuone.util.model;

import java.util.List;

import javapns.notification.PushedNotification;
/**
 * 推送的结果
 * @author Zz
 *
 */
public class AnpsPushResult {
	
	private int serverId;
	private List<PushedNotification> failedNotifications;
	
	private List<PushedNotification> successfulNotifications;

	public List<PushedNotification> getFailedNotifications() {
		return failedNotifications;
	}

	public void setFailedNotifications(List<PushedNotification> failedNotifications) {
		this.failedNotifications = failedNotifications;
	}

	public List<PushedNotification> getSuccessfulNotifications() {
		return successfulNotifications;
	}

	public void setSuccessfulNotifications(List<PushedNotification> successfulNotifications) {
		this.successfulNotifications = successfulNotifications;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	

}

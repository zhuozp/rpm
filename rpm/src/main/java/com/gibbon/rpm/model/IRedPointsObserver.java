package com.gibbon.rpm.model;

public interface IRedPointsObserver {
	void notifyRedPointChange(String redPointId, boolean show, int showNum);
}

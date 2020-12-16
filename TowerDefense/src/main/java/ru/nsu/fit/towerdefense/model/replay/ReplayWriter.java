package ru.nsu.fit.towerdefense.model.replay;

public class ReplayWriter {


	private ReplayWriter instance = null;

	private ReplayWriter()
	{

	}

	public ReplayWriter getInstance()
	{
		if (instance == null) instance = new ReplayWriter();
		return instance;
	}


	public void fullCopy()
	{

	}

	public void registerEvent()
	{

	}


}

package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.List;

public class Report {
	//表集合
	private List<Table> tables = new ArrayList<>();

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
}

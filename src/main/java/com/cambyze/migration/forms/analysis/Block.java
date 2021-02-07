package com.cambyze.migration.forms.analysis;

import java.util.ArrayList;
import java.util.List;

public class Block {

  private String name;
  private String table;
  private List<Trigger> triggers;

  public Block(String name) {
    super();
    this.name = name;
    this.table = "";
    this.triggers = new ArrayList<Trigger>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public List<Trigger> getTriggers() {
    return triggers;
  }

  public void setTriggers(List<Trigger> triggers) {
    this.triggers = triggers;
  }

  @Override
  public String toString() {
    return "Block [name=" + name + ", table=" + table + ", " + triggers.size() + " triggers]";
  }



}

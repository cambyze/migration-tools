package com.cambyze.migration.forms.analysis;

import java.util.ArrayList;
import java.util.List;

public class Form {

  private String name;
  private List<Trigger> triggers;
  private List<Block> blocks;

  public Form(String name) {
    super();
    this.name = name;
    this.triggers = new ArrayList<Trigger>();
    this.blocks = new ArrayList<Block>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Trigger> getTriggers() {
    return triggers;
  }

  public void setTriggers(List<Trigger> triggers) {
    this.triggers = triggers;
  }

  public List<Block> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<Block> blocks) {
    this.blocks = blocks;
  }

  @Override
  public String toString() {
    return "Form [name=" + name + ", " + triggers.size() + " triggers, blocks " + blocks + "]";
  }

}

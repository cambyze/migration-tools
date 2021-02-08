package com.cambyze.migration.forms.analysis;

import java.util.ArrayList;
import java.util.List;

public class Form {

  private String name;
  private List<Trigger> triggers;
  private List<Block> blocks;
  private List<Procedure> procedures;

  public Form(String name) {
    super();
    this.name = name;
    this.triggers = new ArrayList<Trigger>();
    this.blocks = new ArrayList<Block>();
    this.procedures = new ArrayList<Procedure>();
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

  public List<Procedure> getProcedures() {
    return procedures;
  }

  public void setProcedures(List<Procedure> procedures) {
    this.procedures = procedures;
  }

  @Override
  public String toString() {
    return "Form [name=" + name + ", " + triggers.size() + " triggers, blocks=" + blocks
        + ", procedures=" + procedures + "]";
  }

}

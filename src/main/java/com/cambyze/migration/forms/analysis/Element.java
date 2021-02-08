package com.cambyze.migration.forms.analysis;

import java.util.ArrayList;
import java.util.List;

public class Element {

  private String name;
  private List<Trigger> triggers;

  public Element(String name) {
    super();
    this.name = name;
    this.triggers = new ArrayList<Trigger>();
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

  @Override
  public String toString() {
    return "Element [name=" + name + ", " + triggers.size() + " triggers]";
  }



}

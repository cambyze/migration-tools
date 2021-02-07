package com.cambyze.migration.forms.analysis;

import java.util.ArrayList;
import java.util.List;

public class Trigger {

  private String name;
  private List<String> code;



  public Trigger(String name) {
    super();
    this.name = name;
    this.code = new ArrayList<String>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getCode() {
    return code;
  }

  public void setCode(List<String> code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "Trigger [name=" + name + ", code=" + code + "]";
  }


}

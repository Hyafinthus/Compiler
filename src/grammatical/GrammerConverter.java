package grammatical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class GrammerConverter {
  private ArrayList<Production> productions = new ArrayList<Production>();
  private HashSet<String> nonterminals = new HashSet<String>();
  private String start;
  private HashMap<String, HashSet<String>> firstMap = new HashMap<String, HashSet<String>>();
  private HashMap<String, HashSet<String>> followMap = new HashMap<String, HashSet<String>>();
  private HashMap<Production, HashSet<String>> selectMap =
      new HashMap<Production, HashSet<String>>();

  // 内部类产生式
  private class Production {
    private String leftPart;
    private ArrayList<String> rightPart;

    public Production(String leftPart, ArrayList<String> rightPart) {
      this.leftPart = leftPart;
      this.rightPart = new ArrayList<String>();
      this.rightPart.addAll(rightPart);
    }

    public String getLeftPart() {
      return leftPart;
    }

    public ArrayList<String> getRightPart() {
      return rightPart;
    }
  }

  // 读取文法表格，为产生式们赋值
  public GrammerConverter(Vector<String> xlsTitle, Vector<Vector<String>> xlsData) {
    ArrayList<String> rightPart = new ArrayList<String>();
    nonterminals.add(xlsTitle.get(0));
    start = xlsTitle.get(0);
    for (int i = 1; i < xlsTitle.size(); i++) {
      if (xlsTitle.get(i).length() > 0) {
        rightPart.add(xlsTitle.get(i));
      }
    }
    Production production = new Production(xlsTitle.get(0), rightPart);
    productions.add(production);
    for (Vector<String> line : xlsData) {
      nonterminals.add(line.get(0));
      rightPart = new ArrayList<String>();
      for (int i = 1; i < line.size(); i++) {
        if (line.get(i).length() > 0) {
          rightPart.add(line.get(i));
        }
      }
      production = new Production(line.get(0), rightPart);
      productions.add(production);
    }
  }

  public Vector<Vector<String>> getFirstFollowData() {
    for (String nt : nonterminals) {
      firstMap.put(nt, new HashSet<String>());
      followMap.put(nt, new HashSet<String>());
    }
    boolean isDoneFirst = false;
    while (!isDoneFirst) {
      isDoneFirst = true;
      for (Production p : productions) {
        if (!nonterminals.contains(p.rightPart.get(0))) {
          // 加进新符号就说明还未结束
          if (!firstMap.get(p.leftPart).contains(p.rightPart.get(0))) {
            firstMap.get(p.leftPart).add(p.rightPart.get(0));
            isDoneFirst = false;
          }
        } else {
          for (int i = 0; i < p.rightPart.size(); i++) {
            if (!p.rightPart.get(i).contains("ε")) {
              if (!firstMap.get(p.leftPart).containsAll(firstMap.get(p.rightPart.get(i)))) {
                firstMap.get(p.leftPart).addAll(firstMap.get(p.rightPart.get(i)));
                isDoneFirst = false;
              }
              break;
            }
            if (i == p.rightPart.size()) {
              firstMap.get(p.leftPart).add("ε");
            }
          }
        }
      }
    }
    boolean isDoneFollow = false;
    while (!isDoneFollow) {
      isDoneFollow = true;
      for (String nt : nonterminals) {
        if (nt.equals(start) && !followMap.get(nt).contains("$")) {
          System.out.println(1);
          System.out.println(followMap.get(nt));
          System.out.println("$");
          
          followMap.get(nt).add("$");
          isDoneFollow = false;
        }
        for (Production p : productions) {
          for (String rstr : p.getRightPart()) {
            if (rstr.equals(nt)) {
              int rIndex = p.getRightPart().indexOf(nt);
              if (rIndex == p.getRightPart().size() - 1) {
                if (!followMap.get(nt).containsAll(followMap.get(p.getLeftPart()))) {
                  System.out.println(2);
                  System.out.println(followMap.get(nt));
                  System.out.println(followMap.get(p.getLeftPart()));
                  
                  followMap.get(nt).addAll(followMap.get(p.getLeftPart()));
                  isDoneFollow = false;
                }
              } else {
                for (int i = rIndex; i < p.getRightPart().size() - 1; i++) {
                  if (!nonterminals.contains(p.getRightPart().get(i + 1))) {
                    if (!followMap.get(nt).contains(p.getRightPart().get(i + 1))) {
                      System.out.println(3);
                      System.out.println(followMap.get(nt));
                      System.out.println(p.getRightPart().get(i + 1));
                      
                      followMap.get(nt).add(p.getRightPart().get(i + 1));
                      isDoneFollow = false;
                    }
                    break;
                  } else {
                    HashSet<String> followSet = new HashSet<String>();
                    followSet.addAll(firstMap.get(p.getRightPart().get(i + 1)));
                    followSet.remove("ε");
                    if (!followMap.get(nt).containsAll(followSet)) {
                      System.out.println(4);
                      System.out.println(followMap.get(nt));
                      System.out.println(followSet);
                      
                      followMap.get(nt).addAll(followSet);
                      isDoneFollow = false;
                    }
                  }
                  if (!firstMap.get(p.getRightPart().get(i + 1)).contains("ε")) {
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    Vector<Vector<String>> result = new Vector<Vector<String>>();
    for(String nt:nonterminals) {
      Vector<String> line = new Vector<String>();
      line.add(nt);
      String firstSetStr = "{";
      boolean isFst = true;
      for(String first:firstMap.get(nt)) {
        if(isFst) {
          firstSetStr += first;
          isFst = false;
        }else {
          firstSetStr += ","+first;
        }
      }
      firstSetStr+="}";
      line.add(firstSetStr);
      
      String followSetStr = "{";
      isFst = true;
      for(String follow:followMap.get(nt)) {
        if(isFst) {
          followSetStr += follow;
          isFst = false;
        }else {
          followSetStr += ","+follow;
        }
      }
      followSetStr+="}";
      line.add(followSetStr);
      result.add(line);
    }
    return result;

  }


}

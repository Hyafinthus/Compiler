package Lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Dfa2Token {
  public Dfa dfa;
  public File file;
  public Scanner scanner;

  public int lineIndex = 0;
  public int state = 0;
  StringBuilder pending = new StringBuilder(); // 存储一个单词的已读字符

  public List<String> words = new ArrayList<>();
  public List<String> tokens = new ArrayList<>();
  public List<String> indexs = new ArrayList<>();
  public List<String> errors = new ArrayList<>();
  public List<String> reasons = new ArrayList<>();

  public Dfa2Token(Dfa dfa, File file) throws FileNotFoundException {
    this.dfa = dfa;
    this.file = file;
    this.scanner = new Scanner(file);
  }

  public void analysis() {
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      lineIndex++;

      for (int i = 0; i < line.length(); i++) {
        String chara = String.valueOf(line.charAt(i)); // 字符

        if (chara.equals(" ")) {
          endOfWord();
        } else {
          String inputType = Keyword.getInputType(chara);
          Integer nextState = dfa.dfaTable.get(state).get(dfa.dfaInputIndex.get(inputType));
          if (nextState == -1) { // 一个单词读完或出错
            endOfWord();
            goBack(chara);
          } else { // 一个单词未读完
            state = nextState;
            pending.append(chara);
          }
        }
      }

      // 行尾字符处理
      if (pending.length() > 0) {
        endOfWord();
      }
    }
  }

  // 两个单词间没有空格 需要回退一个字符
  public void goBack(String chara) {
    String inputType = Keyword.getInputType(chara);
    state = dfa.dfaTable.get(0).get(dfa.dfaInputIndex.get(inputType));
    pending.append(chara);
  }

  public void endOfWord() {
    if (dfa.isTerminal(state)) { // 终结状态
      String word = pending.toString();
      words.add(word);
      pending = new StringBuilder();

      String specie = dfa.dfaState.get(state);
      String attr = "";

      if (state == 20 && Keyword.operations.contains(word)) { // 双运算符 存在 一词一码
        specie = Keyword.species.get(word);
        attr = "_";
      } else if (state == 20 && !Keyword.operations.contains(word)) { // 双运算符 不存在 错误
        panic();
        return;
      } else if (specie.equals("OPR") || specie.equals("DEL")) { // 运算符 界符 一词一码
        specie = Keyword.species.get(word);
        attr = "_";
      } else if (Keyword.keywords.contains(word)) { // 关键字 一词一码
        specie = word.toUpperCase();
        attr = "_";
      } else { // 多词一码
        specie = dfa.dfaState.get(state);
        attr = word;
      }

      String token = "< " + specie + ", " + attr + " >";
      tokens.add(token);

      indexs.add(String.valueOf(lineIndex));

      state = 0;
    } else { // 非终结状态
      panic();
    }
  }

  public void panic() {
    System.out.println("PANIC");
  }
}

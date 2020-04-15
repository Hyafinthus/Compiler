package lexical;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Dfa2Token {
  public Dfa dfa;
  public String[] lines;

  public int lineIndex = 0;
  public int charIndex = 0;
  public int state = 0;
  StringBuilder pending = new StringBuilder(); // 存储一个单词的已读字符

  public List<String> words = new ArrayList<>();
  public List<String> tokens = new ArrayList<>();
  public List<String> tokenIndexs = new ArrayList<>();

  public List<String> errors = new ArrayList<>();
  public List<String> reasons = new ArrayList<>();
  public List<String> errorIndexs = new ArrayList<>();

  public Dfa2Token(Dfa dfa, String[] lines) throws FileNotFoundException {
    this.dfa = dfa;
    this.lines = lines;
  }

  // 对代码文件进行分析
  public void analysis() {
    for (lineIndex = 0; lineIndex < lines.length; lineIndex++) {
      String line = lines[lineIndex].trim().replaceAll("\t", " ").replaceAll(" +", " ");

      for (charIndex = 0; charIndex < line.length(); charIndex++) {
        String chara = String.valueOf(line.charAt(charIndex)); // 字符

        if (chara.equals(" ")) {
          endOfWord();
        } else {
          String inputType = Keyword.getInputType(chara);
          Integer nextState = dfa.dfaTable.get(state).get(dfa.dfaInputIndex.get(inputType));
          if (nextState == -1) { // 一个单词读完或出错
            // 行首错误 需恐慌模式
            if (state == 0) {
              pending.append(chara);
              panic(0);
              continue;
            }

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

    if (state == -1) {
      panic(1);
    }
  }

  // 一个单词读取结束
  public void endOfWord() {
    if (dfa.isTerminal(state)) { // 终结状态
      String word = pending.toString();

      String specie = dfa.dfaState.get(state);
      String attr = "";

      if (specie.equals("DOP") && Keyword.operations.contains(word)) { // 双运算符 存在 一词一码
        specie = Keyword.species.get(word);
        attr = "_";
      } else if (specie.equals("DOP") && !Keyword.operations.contains(word)) { // 双运算符 不存在 错误
        panic(2);
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

      pending = new StringBuilder();

      if (!specie.equals("CMT")) { // 跳过注释
        words.add(word);

        // String token = "< " + specie + ", " + attr + " >";
        // tokens.add(token);
        tokens.add(specie);

        tokenIndexs.add(String.valueOf(lineIndex + 1));
      }

      state = 0;
    } else { // 非终结状态
      panic(-1);
    }
  }

  // 0:行首错误 1:词首错误 2:双运算符错误 -1:DFA标注错误
  public void panic(int type) {
    String error = pending.toString();
    errors.add(error);
    pending = new StringBuilder();

    String reason = "";
    switch (type) {
      case 0:
        reason = "行首错误";
        break;
      case 1:
        reason = "词首错误";
        break;
      case 2:
        reason = "双运算符错误";
        break;
      default:
        reason = dfa.dfaState.get(state);
        break;
    }
    reasons.add(reason);

    errorIndexs.add(String.valueOf(lineIndex + 1));

    restore();
  }

  // 从错误中恢复 采用恐慌模式
  public void restore() {
    state = 0;
    String line = this.lines[this.lineIndex].trim().replaceAll("\t", " ").replaceAll(" +", " ");
    while (true) {
      charIndex++;
      if (charIndex == line.length()) {
        break;
      }
      String chara = String.valueOf(line.charAt(charIndex));
      String inputType = Keyword.getInputType(chara);
      Integer nextState = dfa.dfaTable.get(0).get(dfa.dfaInputIndex.get(inputType));
      if (nextState != -1) {
        charIndex--;
        break;
      }
    }
  }

  public Vector<Vector<String>> getTokenData() {
    Vector<Vector<String>> tokenData = new Vector<>();
    for (int i = 0; i < this.words.size(); i++) {
      Vector<String> tokenLine = new Vector<>();
      tokenLine.add(this.tokenIndexs.get(i));
      tokenLine.add(this.words.get(i));
      tokenLine.add(this.tokens.get(i));
      tokenData.add(tokenLine);
    }
    return tokenData;
  }

  public Vector<Vector<String>> getErrorData() {
    Vector<Vector<String>> errorData = new Vector<>();
    for (int i = 0; i < this.errors.size(); i++) {
      Vector<String> errorLine = new Vector<>();
      errorLine.add(this.errorIndexs.get(i));
      errorLine.add(this.errors.get(i));
      errorLine.add(this.reasons.get(i));
      errorData.add(errorLine);
    }
    return errorData;
  }
}

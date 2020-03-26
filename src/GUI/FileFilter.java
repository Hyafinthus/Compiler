package GUI;

import java.io.File;
import java.util.ArrayList;

public class FileFilter extends javax.swing.filechooser.FileFilter {
  private ArrayList<String> list = new ArrayList<String>();;

  public FileFilter(ArrayList<String> list) {
    this.list = list;
  }

  @Override
  public boolean accept(File f) {
    int i;
    if (f.isDirectory())
      return true;
    for (i = 0; i < list.size() - 1; i++) {
      if (f.getName().endsWith(list.get(i)))
        return true;
    }

    return f.getName().endsWith(list.get(i));
  }

  @Override
  public String getDescription() {
    return "";
  }

}

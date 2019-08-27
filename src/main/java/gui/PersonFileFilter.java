package gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PersonFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if(f.isDirectory()){
            return true;
        }
        String name = f.getName();
        String extension = Utils.getFileExtension(name);
        if (extension == null){
            return false;
        }
        return extension.equals("per");
    }

    @Override
    public String getDescription() {
        return "Person Database Files (*.per)";
    }
}
